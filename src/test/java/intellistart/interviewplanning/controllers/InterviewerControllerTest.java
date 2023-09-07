//package com.intellias.intellistart.interviewplanning.controllers;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import booking.model.intellistart.interviewplanning.BookingService;
//import interviewerslot.model.intellistart.interviewplanning.InterviewerSlotRepository;
//import interviewerslot.model.intellistart.interviewplanning.InterviewerSlotService;
//import user.model.intellistart.interviewplanning.UserRepository;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.Setter;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//@AutoConfigureMockMvc
//@SpringBootTest
//public class InterviewerControllerTest {
//  static UserRepository userRepository = Mockito.mock(UserRepository.class);
//  static BookingService bookingService = Mockito.mock(BookingService.class);
//
//  static InterviewerSlotRepository interviewerSlotRepository =
//      Mockito.mock(InterviewerSlotRepository.class);
//
//
//  @Autowired
//  private MockMvc mockMvc;
//
//  @Autowired
//  private ObjectMapper objectMapper;
//
//  static InterviewerSlotService interviewerSlotService;
//
//
//  @AllArgsConstructor
//  @Getter
//  @Setter
//  static class JsonObj {
//    Long week;
//    String dayOfWeek;
//    String from;
//    String to;
//  }
//
//  @BeforeAll
//  static void initialize() {
//    interviewerSlotRepository = Mockito.mock(InterviewerSlotRepository.class);
//    interviewerSlotService = new InterviewerSlotService(
//        interviewerSlotRepository, bookingService
//    );
//  }
//
//  @Test
//  void postRequestWithNullDayAngGetBadRequest() throws Exception {
//    JsonObj jsonObj = new JsonObj(50L, null, "10:00", "20:00");
//    mockMvc.perform(MockMvcRequestBuilders.post("/interviewers/{interviewerId}/slots", 1L)
//            .content(objectMapper.writeValueAsString(jsonObj))
//            .contentType(MediaType.APPLICATION_JSON)
//            .accept(MediaType.APPLICATION_JSON))
//        .andExpect(status().isBadRequest());
//  }
//
//  @Test
//  void postRequestWithNullFromAngGetBadRequest() throws Exception {
//    JsonObj jsonObj = new JsonObj(50L, "THU", null, "20:00");
//    mockMvc.perform(MockMvcRequestBuilders.post("/interviewers/{interviewerId}/slots", 1L)
//            .content(objectMapper.writeValueAsString(jsonObj))
//            .contentType(MediaType.APPLICATION_JSON)
//            .accept(MediaType.APPLICATION_JSON))
//        .andExpect(status().isBadRequest());
//  }
//
//  @Test
//  void postRequestWithNullToAngGetBadRequest() throws Exception {
//    JsonObj jsonObj = new JsonObj(50L, "SUN", "10:00", null);
//    mockMvc.perform(MockMvcRequestBuilders.post("/interviewers/{interviewerId}/slots", 1L)
//            .content(objectMapper.writeValueAsString(jsonObj))
//            .contentType(MediaType.APPLICATION_JSON)
//            .accept(MediaType.APPLICATION_JSON))
//        .andExpect(status().isBadRequest());
//  }
//}
//
//
