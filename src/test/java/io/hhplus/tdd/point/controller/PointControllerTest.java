package io.hhplus.tdd.point.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.tdd.point.controller.request.UserPointRequest;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.service.PointManageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static io.hhplus.tdd.point.domain.type.TransactionType.CHARGE;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = PointController.class)
class PointControllerTest {

    /**
     * 클라이언트의 요청 값과 설계한대로의 응답도 정상 동작 하는지도 검증이 필요하기 때문에
     * @WebMvcTest를 사용하여 컨트롤러 통합테스트를 작성했습니다.
     */

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private PointManageService pointManageService;

    @DisplayName("요청받은 id를 통해 유저의 포인트를 조회한다.")
    @Test
    void getUserPointTest() throws Exception{
        //given
        long id = 1L;
        long amount = 1000L;
        UserPoint userPoint = new UserPoint(id, amount, System.currentTimeMillis());
        when(pointManageService.getUserPoint(id)).thenReturn(userPoint);

        //when //then
        mockMvc.perform(get("/point/" +id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.point").value(amount));

    }

    @DisplayName("요청받은 id를 통해 유저의 충전/이용 내역 조회한다.")
    @Test
    void getPointHistoryTest() throws Exception{
        //given
        long cursor = 1L;
        long id = 2L;
        long amount = 1000L;
        PointHistory pointHistory = new PointHistory(cursor, id, amount, CHARGE, System.currentTimeMillis());
        when(pointManageService.getPointHistoriesByUserId(id)).thenReturn(List.of(pointHistory));

        //when //then
        mockMvc.perform(get("/point/" +id +"/histories"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].amount").value(amount))
                .andExpect(jsonPath("$[0].type").value(String.valueOf(CHARGE)));

    }

    @DisplayName("유저가 포인트를 충전 또는 사용할 때 금액은 양수여야 한다.")
    @Test
    void useOrChargeUserPointWithoutZeroAmountTest() throws Exception{
        //given
        long id = 1L;
        long amount = 0L;
        UserPointRequest request = new UserPointRequest(amount);

        //when //then
        mockMvc.perform(
                        patch("/point/" +id +"/charge")
                                .content(mapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("요청받은 id와 금액을 통해 유저의 포인트를 충전한다.")
    @Test
    void chargeUserPointTest() throws Exception{
        //given
        long id = 1L;
        long amount = 1000L;
        UserPointRequest request = new UserPointRequest(amount);

        //when //then
        mockMvc.perform(
                    patch("/point/" +id +"/charge")
                            .content(mapper.writeValueAsString(request))
                            .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("요청받은 id와 금액을 통해 유저의 포인트를 사용한다.")
    @Test
    void useUserPointTest() throws Exception{
        //given
        long id = 1L;
        long amount = 1000L;
        UserPointRequest request = new UserPointRequest(amount);

        //when //then
        mockMvc.perform(
                        patch("/point/" +id +"/use")
                                .content(mapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());

    }



}