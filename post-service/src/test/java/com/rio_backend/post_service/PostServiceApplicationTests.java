//package com.rio_backend.post_service;
//
//import com.rio_backend.post_service.dto.PostRequest;
//import com.rio_backend.post_service.enums.Category;
//import com.rio_backend.post_service.repository.PostRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import org.testcontainers.mongodb.MongoDBContainer;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//
//
//@SpringBootTest
//@Testcontainers
//@AutoConfigureMockMvc
//class PostServiceApplicationTests {
//
//    @Container
//    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    PostRepository postRepository;
//
//    @DynamicPropertySource
//    static void setProperties(DynamicPropertyRegistry dymDynamicPropertyRegistry){
//        dymDynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
//    }
//
//	@Test
//	void shouldCreatePost() throws Exception {
//        PostRequest postRequest = getPostRequest();
//        String postRequestString = objectMapper.writeValueAsString(postRequest);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/add")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(postRequestString))
//                .andExpect(status().isCreated());
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/post")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].authorId").value("abc"))
//                .andExpect(jsonPath("$[0].username").value("sarthak"))
//                .andExpect(jsonPath("$[0].category").value("TECHNOLOGY"))
//                .andExpect(jsonPath("$[0].content").value("Testing the request"));
//
//	}
//
//    @Test
//    void shouldGetUserPosts() throws Exception {
//        // Create post first
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/add")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(getPostRequest())))
//                .andExpect(status().isCreated());
//
//        // Fetch posts by author
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/user")
//                        .param("authorId", "abc"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].authorId").value("abc"))
//                .andExpect(jsonPath("$[0].username").value("sarthak"));
//    }
//
//    @Test
//    void shouldGetPostsByCategory() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/add")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(getPostRequest())))
//                .andExpect(status().isCreated());
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/category")
//                        .param("category", "TECHNOLOGY"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].category").value("TECHNOLOGY"));
//    }
//
//    @Test
//    void shouldLikePost() throws Exception {
//        // First create the post
//        String postRequestString = objectMapper.writeValueAsString(getPostRequest());
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/add")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(postRequestString))
//                .andExpect(status().isCreated());
//
//        String postId = postRepository.findAll().getFirst().getId();
//
//        // Like the post
//        mockMvc.perform(MockMvcRequestBuilders.patch("/api/post/{postId}/like", postId))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Post liked successfully"));
//    }
//
//    @Test
//    void shouldDislikePost() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/add")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(getPostRequest())))
//                .andExpect(status().isCreated());
//
//        String postId = postRepository.findAll().getFirst().getId();
//
//        mockMvc.perform(MockMvcRequestBuilders.patch("/api/post/{postId}/dislike", postId))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Post disliked successfully"));
//    }
//
//    @Test
//    void shouldDeletePost() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/add")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(getPostRequest())))
//                .andExpect(status().isCreated());
//
//        String postId = postRepository.findAll().getFirst().getId();
//
//        mockMvc.perform(MockMvcRequestBuilders.delete("/api/post/delete/{postId}", postId)
//                        .param("authorId", "abc"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Post deleted successfully."));
//
//        // Confirm post actually removed
//        assert postRepository.findById(postId).isEmpty();
//    }
//
//    private PostRequest getPostRequest() {
//        return PostRequest.builder()
//                .authorId("abc")
//                .username("sarthak")
//                .content("Testing the request")
//                .category(Category.TECHNOLOGY)
//                .build();
//    }
//
//}
