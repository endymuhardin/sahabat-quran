package com.sahabatquran.webapp.service;

import com.sahabatquran.webapp.dto.ExamQuestionDto;
import com.sahabatquran.webapp.entity.Exam;
import com.sahabatquran.webapp.entity.ExamQuestion;
import com.sahabatquran.webapp.entity.User;
import com.sahabatquran.webapp.repository.ExamRepository;
import com.sahabatquran.webapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExamQuestionService {
    
    private final ExamRepository examRepository;
    private final UserRepository userRepository;
    
    public ExamQuestionDto addQuestion(UUID examId, ExamQuestionDto questionDto, String createdByUsername) {
        log.info("Adding question to exam: {}", examId);
        
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found: " + examId));
        
        if (!exam.isEditable()) {
            throw new RuntimeException("Cannot modify exam in current status: " + exam.getStatus());
        }
        
        User createdBy = userRepository.findByUsername(createdByUsername)
                .orElseThrow(() -> new RuntimeException("User not found: " + createdByUsername));
        
        ExamQuestion question = new ExamQuestion();
        question.setQuestionNumber(questionDto.getQuestionNumber());
        question.setQuestionType(ExamQuestion.QuestionType.valueOf(questionDto.getQuestionType()));
        question.setQuestionText(questionDto.getQuestionText());
        question.setPoints(questionDto.getPoints());
        question.setRequired(questionDto.getRequired());
        question.setQuestionData(questionDto.getQuestionData());
        question.setExplanation(questionDto.getExplanation());
        question.setTimeLimitSeconds(questionDto.getTimeLimitSeconds());
        question.setCreatedBy(createdBy);
        
        exam.addQuestion(question);
        examRepository.save(exam);
        
        return convertToDto(question);
    }
    
    @Transactional(readOnly = true)
    public List<ExamQuestionDto> getQuestionsByExamId(UUID examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found: " + examId));
        
        return exam.getQuestions().stream()
                .sorted((q1, q2) -> Integer.compare(q1.getQuestionNumber(), q2.getQuestionNumber()))
                .map(this::convertToDto)
                .toList();
    }
    
    private ExamQuestionDto convertToDto(ExamQuestion question) {
        ExamQuestionDto dto = new ExamQuestionDto();
        dto.setId(question.getId());
        dto.setExamId(question.getExam().getId());
        dto.setQuestionNumber(question.getQuestionNumber());
        dto.setQuestionType(question.getQuestionType().name());
        dto.setQuestionText(question.getQuestionText());
        dto.setPoints(question.getPoints());
        dto.setRequired(question.getRequired());
        dto.setQuestionData(question.getQuestionData());
        dto.setExplanation(question.getExplanation());
        dto.setTimeLimitSeconds(question.getTimeLimitSeconds());
        dto.setCreatedBy(question.getCreatedBy().getUsername());
        dto.setCreatedAt(question.getCreatedAt());
        
        // Extract type-specific data from questionData
        Map<String, Object> data = question.getQuestionData();
        if (data != null) {
            switch (question.getQuestionType()) {
                case MULTIPLE_CHOICE:
                case MULTIPLE_SELECT:
                    dto.setOptions(question.getMultipleChoiceOptions());
                    dto.setCorrectAnswers(question.getCorrectAnswers());
                    break;
                case TRUE_FALSE:
                    dto.setTrueFalseAnswer(question.getTrueFalseAnswer());
                    break;
                case SHORT_ANSWER:
                case ESSAY:
                    dto.setMaxWords((Integer) data.getOrDefault("maxWords", 
                            question.getQuestionType() == ExamQuestion.QuestionType.SHORT_ANSWER ? 50 : 500));
                    break;
                case FILL_BLANK:
                    dto.setBlankAnswers(question.getFillBlankAnswers());
                    break;
                case MATCHING:
                    dto.setMatchingPairs(question.getMatchingPairs());
                    break;
            }
        }
        
        return dto;
    }
}