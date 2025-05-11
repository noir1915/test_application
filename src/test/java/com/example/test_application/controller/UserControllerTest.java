//package com.example.test_application.controller;
//
//import com.example.test_application.dto.UserDto;
//import com.example.test_application.service.UserService;
//import org.junit.jupiter.api.*;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.springframework.test.web.servlet.MockMvc;
//import org.testcontainers.containers.PostgreSQLContainer;
//
//
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureMockMvc
//public class UserControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Mock
//    private UserService userService;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    public static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:latest");
//
//    @BeforeAll
//    public static void beforeAll() {
//        container.start();
//    }
//
//    @AfterAll
//    public static void afterAll() {
//        container.stop();
//    }
//
//    @DynamicPropertySource
//    public static void configureProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", container::getJdbcUrl);
//        registry.add("spring.datasource.username", container::getUsername);
//        registry.add("spring.datasource.password", container::getPassword);
//    }
//
//    @Test
//    public void testGetUserById() throws Exception {
//        UserDto userDto = new UserDto();
//        userDto.setId(1L);
//        userDto.setName("John Doe");
//
//        when(userService.getUserById(anyLong())).thenReturn(userDto);
//
//        mockMvc.perform(get("/api/v1/users/1")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(result -> {
//                    String content = result.getResponse().getContentAsString();
//                    Assertions.assertTrue(content.contains("John Doe"));
//                });
//    }
//
//    @Test
//    public void testCreateUser() throws Exception {
//        UserDto userDto = new UserDto();
//        userDto.setName("Jane Doe");
//
//        doNothing().when(userService).createUser(any());
//
//        mockMvc.perform(post("/api/v1/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"name\":\"Jane Doe\"}"))
//                .andExpect(status().isCreated());
//    }
//}