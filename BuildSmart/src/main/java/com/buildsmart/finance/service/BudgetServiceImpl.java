package com.buildsmart.finance.service;

import com.buildsmart.common.exception.ResourceNotFoundException;
import com.buildsmart.finance.dto.BudgetRequestDto;
import com.buildsmart.finance.dto.BudgetResponseDto;
import com.buildsmart.finance.entity.Budget;
import com.buildsmart.finance.repository.BudgetRepository;
import com.buildsmart.finance.validator.BudgetValidator;
import com.buildsmart.projectmanager.entity.Project;
import com.buildsmart.projectmanager.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final BudgetIdGeneratorService budgetIdGeneratorService;
    private final BudgetValidator budgetValidator;
    private final ProjectRepository projectRepository;

    @Override
    @Transactional
    public BudgetResponseDto createBudget(BudgetRequestDto request) {
        budgetValidator.validateCreate(request);
        Project project = projectRepository.findByProjectId(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", request.getProjectId()));
        String budgetId = budgetIdGeneratorService.generateNextBudgetId();
        BigDecimal actualAmount = request.getActualAmount() != null ? request.getActualAmount() : BigDecimal.ZERO;
        Budget budget = Budget.builder()
                .budgetId(budgetId)
                .project(project)
                .category(request.getCategory())
                .plannedAmount(request.getPlannedAmount())
                .actualAmount(actualAmount)
                .build();
        budget.calculateVariance();
        Budget saved = budgetRepository.save(budget);
        return toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BudgetResponseDto getBudgetById(String budgetId) {
        Budget budget = budgetRepository.findByBudgetId(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", budgetId));
        return toResponseDto(budget);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BudgetResponseDto> getBudgetsByProject(String projectId, Pageable pageable) {
        return budgetRepository.findByProject_ProjectId(projectId, pageable)
                .map(this::toResponseDto);
    }

    @Override
    @Transactional
    public BudgetResponseDto updateBudget(String budgetId, BudgetRequestDto request) {
        budgetValidator.validateUpdate(request, budgetId);
        Budget budget = budgetRepository.findByBudgetId(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", budgetId));
        Project project = budget.getProject();
        if (!project.getProjectId().equals(request.getProjectId())) {
            project = projectRepository.findByProjectId(request.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project", request.getProjectId()));
        }
        budget.setProject(project);
        budget.setCategory(request.getCategory());
        budget.setPlannedAmount(request.getPlannedAmount());
        budget.setActualAmount(request.getActualAmount() != null ? request.getActualAmount() : BigDecimal.ZERO);
        budget.calculateVariance();
        Budget saved = budgetRepository.save(budget);
        return toResponseDto(saved);
    }

    @Override
    @Transactional
    public void deleteBudget(String budgetId) {
        if (!budgetRepository.existsByBudgetId(budgetId)) {
            throw new ResourceNotFoundException("Budget", budgetId);
        }
        budgetRepository.deleteById(budgetId);
    }

    private BudgetResponseDto toResponseDto(Budget budget) {
        return BudgetResponseDto.builder()
                .budgetId(budget.getBudgetId())
                .projectId(budget.getProject().getProjectId())
                .projectName(budget.getProject().getProjectName())
                .category(budget.getCategory())
                .plannedAmount(budget.getPlannedAmount())
                .actualAmount(budget.getActualAmount())
                .variance(budget.getVariance())
                .build();
    }
}
