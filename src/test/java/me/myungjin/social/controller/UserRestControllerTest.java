package me.myungjin.social.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.myungjin.social.model.api.request.JoinRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserRestControllerTest {
    @LocalServerPort
    private int port;

    private MockMvc mvc;



    @Autowired
    private WebApplicationContext context;

    @Before
    public void setup(){
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @Test
    public void 모든_사용자를_조회한다() throws Exception {
        //given
        String url = "http://localhost:" + port + "/api/users/";

        //when
        mvc.perform(get(url))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    public void 신규_사용자를_저장한다() throws Exception {
        //given
        String name = "mjkim";
        String principal = "abc002@gmail.com";
        String credentials = "user001";
        JoinRequest request = new JoinRequest(name, principal, credentials);
        String url = "http://localhost:" + port + "/api/user/join";

        //when
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
