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
}