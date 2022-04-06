package com.example.demo.service;

import com.example.demo.model.TodoEntity;
import com.example.demo.persistence.TodoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TodoService {

    @Autowired
    private TodoRepository repository;

    public String testService() {
        // TodoEntity 생성
        TodoEntity entity = TodoEntity.builder().title("My first todo item").build();
        // 저장
        repository.save(entity);
        // 검색
        TodoEntity savedEntity = repository.findById(entity.getId()).get();
        return savedEntity.getTitle();
    }

    // 유효성검사
    private void validate(final TodoEntity entity){
        if(entity == null){
            log.warn("Entity can't be null");
            throw new RuntimeException("Entity cannot be null");
        }

        if(entity.getUserId() == null){
            log.warn("Unknown user");
            throw new RuntimeException("Unknown user");
        }
    }

    public List<TodoEntity> create(final TodoEntity entity){
        validate(entity);

        repository.save(entity);
        log.info("Entity Id : {} is saved.", entity.getId());
        return repository.findByUserId(entity.getUserId());
    }

    public List<TodoEntity> retrieve(final String userId){
        return repository.findByUserId(userId);
    }

    public List<TodoEntity> update(final TodoEntity entity){
        validate(entity);
        // 요청에 있는 엔티티 id로 해당 엔티티를 가져온다
        final Optional<TodoEntity> original = repository.findById(entity.getId());
        original.ifPresent(todo -> {
            // 해당 엔티티가 존재하는경우 (id가 유효값인 경우) 수정
            todo.setTitle(entity.getTitle());
            todo.setDone(entity.isDone());

            // 수정된 값을 저장
            repository.save(todo);
        });

        // 리스트로 리턴
        return retrieve(entity.getUserId());
    }

    public List<TodoEntity> delete(final TodoEntity entity){
        validate(entity);

        try {
            repository.delete(entity);
        } catch (Exception e) {
            log.error("error deleting entity ", entity.getId(), e);
            throw new RuntimeException("error deleting entity " + entity.getId());
        }

        return retrieve(entity.getUserId());
    }
}
