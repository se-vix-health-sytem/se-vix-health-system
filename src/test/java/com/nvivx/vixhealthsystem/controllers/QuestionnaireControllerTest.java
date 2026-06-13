package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.controllers.site.QuestionnaireController;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuestionnaireControllerTest {

    private final QuestionnaireController controller = new QuestionnaireController();

    @Test
    void shouldShowQuestionnairePage() {

        Model model = mock(Model.class);

        String view = controller.showQuestionnaire(model);

        assertEquals("questionnaire/index", view);

        verify(model).addAttribute(eq("pageTitle"), any());
        verify(model).addAttribute(eq("symptomCategories"), any());
    }

    @Test
    void shouldProcessQuestionnaire() {

        Model model = mock(Model.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String view = controller.processQuestionnaire(
                "chest_pain",
                "high",
                "days",
                model,
                redirectAttributes
        );

        assertEquals("questionnaire/result", view);

        verify(model).addAttribute(eq("recommendation"), any(Map.class));
        verify(model).addAttribute(eq("pageTitle"), any());
    }
}