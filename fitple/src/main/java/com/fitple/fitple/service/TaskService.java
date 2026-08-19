package com.fitple.fitple.service;

import com.fitple.fitple.domain.Member;
import com.fitple.fitple.domain.Project;
import com.fitple.fitple.domain.Task;
import com.fitple.fitple.dto.request.TaskCreateRequest;
import com.fitple.fitple.dto.response.TaskResponse;
import com.fitple.fitple.repository.MemberRepository;
import com.fitple.fitple.repository.ProjectRepository;
import com.fitple.fitple.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fitple.fitple.dto.request.TaskStatusUpdateRequest;
import java.util.List;
import java.time.LocalDate;
import com.fitple.fitple.dto.response.TodayTaskResponse;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public TaskResponse createTask(TaskCreateRequest request) {

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 프로젝트입니다."));

        Member member = memberRepository.findById(request.getAssigneeId())
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 회원입니다."));

        Task task = Task.builder()
                .project(project)
                .assignee(member)
                .title(request.getTitle())
                .dueDate(request.getDueDate())
                .status("TODO")
                .build();

        taskRepository.save(task);

        return TaskResponse.from(task);
    }
    @Transactional
    public TaskResponse updateTaskStatus(
            Long taskId,
            TaskStatusUpdateRequest request
    ) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 과제입니다."));

        task.setStatus(request.getStatus().name());

        return TaskResponse.from(task);
    }

    /**
     * 홈 화면용 "오늘의 과제" 조회.
     * 해당 회원의 전체 과제 중, 마감일(dueDate)이 오늘이거나 아직 지나지 않은 것만
     * 필터링해서 마감 임박한 순서로 반환한다. (이미 마감 지난 과제는 제외)
     */
    @Transactional(readOnly = true)
    public List<TodayTaskResponse> getTodayTasks(Long memberId) {

        List<Task> tasks = taskRepository.findByAssigneeId(memberId);

        return tasks.stream()
                .filter(task -> task.getDueDate() != null
                        && !task.getDueDate().isBefore(LocalDate.now())) // 오늘 포함, 이미 지난 건 제외
                .sorted((a, b) -> a.getDueDate().compareTo(b.getDueDate())) // 마감 임박한 순
                .map(TodayTaskResponse::from)
                .toList();
    }

    // 오늘의 과제(마이페이지-전체
    @Transactional(readOnly = true)
    public List<TaskResponse> getMyTasks(
            Long memberId,
            String status
    ) {

        List<Task> tasks;

        if (status == null || status.isBlank()) {
            tasks = taskRepository.findByAssigneeId(memberId);
        } else {
            tasks = taskRepository.findByAssigneeIdAndStatus(
                    memberId,
                    status
            );
        }

        return tasks.stream()
                .map(TaskResponse::from)
                .toList();
    }
    //오늘의 과제(해당프로젝트)
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasks(Long memberId, String status) {

        List<Task> tasks;

        if (status == null || status.equals("ALL")) {
            tasks = taskRepository.findByAssigneeId(memberId);
        } else {
            tasks = taskRepository.findByAssigneeIdAndStatus(
                    memberId,
                    status
            );
        }

        return tasks.stream()
                .map(TaskResponse::from)
                .toList();
    }
    @Transactional
    public TaskResponse updateTaskStatus(Long taskId, String status) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("과제를 찾을 수 없습니다."));

        task.setStatus(status);

        return TaskResponse.from(task);
    }
    @Transactional(readOnly = true)
    public List<TaskResponse> getProjectTodayTasks(
            Long projectId,
            String status
    ) {

        List<Task> tasks;

        if (status == null || status.isBlank() || status.equals("ALL")) {
            tasks = taskRepository.findByProjectIdAndDueDate(
                    projectId,
                    LocalDate.now()
            );
        } else {
            tasks = taskRepository.findByProjectIdAndDueDateAndStatus(
                    projectId,
                    LocalDate.now(),
                    status
            );
        }

        return tasks.stream()
                .map(TaskResponse::from)
                .toList();
    }
    @Transactional
    public TaskResponse updateProjectTaskStatus(
            Long projectId,
            Long taskId,
            String status
    ) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "존재하지 않는 과제입니다."
                        ));

        if (!task.getProject().getId().equals(projectId)) {
            throw new IllegalArgumentException(
                    "해당 프로젝트의 과제가 아닙니다."
            );
        }

        task.setStatus(status);

        return TaskResponse.from(task);
    }
    @Transactional(readOnly = true)
    public TaskResponse getTask(Long taskId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "존재하지 않는 과제입니다."
                        ));

        return TaskResponse.from(task);
    }
    @Transactional(readOnly = true)
    public List<TaskResponse> getProjectTasks(
            Long projectId,
            Long memberId,
            String status
    ) {

        List<Task> tasks;

        if (status == null || status.equals("ALL")) {
            tasks = taskRepository.findByProjectIdAndAssigneeId(
                    projectId,
                    memberId
            );
        } else {
            tasks = taskRepository.findByProjectIdAndAssigneeIdAndStatus(
                    projectId,
                    memberId,
                    status
            );
        }

        return tasks.stream()
                .map(TaskResponse::from)
                .toList();
    }
    @Transactional
    public TaskResponse completeProjectTask(
            Long projectId,
            Long taskId,
            String status
    ) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "존재하지 않는 과제입니다."
                        ));

        if (!task.getProject().getId().equals(projectId)) {
            throw new IllegalArgumentException(
                    "해당 프로젝트의 과제가 아닙니다."
            );
        }

        task.setStatus(status);

        return TaskResponse.from(task);
    }
}