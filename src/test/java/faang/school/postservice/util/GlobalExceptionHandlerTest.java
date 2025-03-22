package faang.school.postservice.util;

import faang.school.postservice.config.context.UserContext;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestController.class)
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserContext userContext;

    @Test
    @DisplayName("Проверка обработки исключения DataValidationException: возврат статуса 400 и сообщения об ошибке")
    public void givenDataValidationHandlerWhenHandleDataValidationExceptionThenReturnDataValidationException() throws Exception {
        mockMvc.perform(get("/test/data-validation"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Ошибка валидации данных: Invalid data"));
    }

    @Test
    @DisplayName("Проверка обработки исключения EntityNotFoundException: возврат статуса 404 и сообщения об ошибке")
    public void givenEntityNotFoundHandlerWhenHandleEntityNotFoundExceptionThenReturnEntityNotFoundException() throws Exception {
        mockMvc.perform(get("/test/entity-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Сущность не найдена: Entity not found"));
    }

    @Test
    @DisplayName("Проверка обработки общего исключения: возврат статуса 500 и сообщения об ошибке")
    public void givenExceptionHandlerWhenHandleExceptionThenException() throws Exception {
        mockMvc.perform(get("/test/exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Произошла внутренняя ошибка: Internal error"));
    }
}

/**
 * Тестовый контроллер для проверки GlobalExceptionHandler
 */
@RestController
@RequestMapping("/test")
class TestController {

    @GetMapping("/data-validation")
    public void throwDataValidationException() {
        throw new DataValidationException("Invalid data");
    }

    @GetMapping("/entity-not-found")
    public void throwEntityNotFoundException() {
        throw new EntityNotFoundException("Entity not found");
    }

    @GetMapping("/exception")
    public void throwException() {
        throw new RuntimeException("Internal error");
    }

    @GetMapping("/method-argument-not-valid")
    public void throwMethodArgumentNotValidException() {
        throw new RuntimeException("Method argument not valid");
    }
}


