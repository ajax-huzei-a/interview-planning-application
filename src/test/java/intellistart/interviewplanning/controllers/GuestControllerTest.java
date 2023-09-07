//package com.intellias.intellistart.interviewplanning.controllers;
//
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import week.model.intellistart.interviewplanning.Week;
//import week.model.intellistart.interviewplanning.WeekService;
//import java.util.HashSet;
//import org.hamcrest.Matchers;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//@AutoConfigureMockMvc
//@SpringBootTest
//class GuestControllerTest {
//
//  @Autowired
//  private MockMvc mockMvc;
//
//  @MockBean
//  private WeekService weekService;
//
//  @Test
//  void shouldGetCurrentWeek() throws Exception {
//    when(weekService.getCurrentWeek()).thenReturn(new Week(42L,new HashSet<>()));
//    mockMvc.perform(MockMvcRequestBuilders.get("/weeks/current"))
//        .andExpect(status().isOk())
//        .andExpect(jsonPath("$.weekNum", Matchers.is(42)))
//        .andExpect(jsonPath("$.*",Matchers.hasSize(1)));
//    verify(weekService).getCurrentWeek();
//  }
//
//  @Test
//  void shouldGetNextWeek() throws Exception {
//    when(weekService.getNextWeek()).thenReturn(new Week(43L,new HashSet<>()));
//    mockMvc.perform(MockMvcRequestBuilders.get("/weeks/next"))
//        .andExpect(status().isOk())
//        .andExpect(jsonPath("$.weekNum", Matchers.is(43)))
//        .andExpect(jsonPath("$.*",Matchers.hasSize(1)));
//    verify(weekService).getNextWeek();
//  }
//}