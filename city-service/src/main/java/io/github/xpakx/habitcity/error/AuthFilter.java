package io.github.xpakx.habitcity.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.xpakx.habitcity.error.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String headerId = request.getHeader("id");
        if(headerId == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(constructErrorBody());
            return;
        }
        logger.info("Successfully authenticated user with id " + headerId);
        filterChain.doFilter(request, response);
    }

    private String constructErrorBody() throws JsonProcessingException {
        ErrorResponse errorBody = new ErrorResponse();
        errorBody.setMessage("User unauthorized!");
        errorBody.setStatus(HttpStatus.UNAUTHORIZED.getReasonPhrase());
        errorBody.setError(HttpStatus.UNAUTHORIZED.value());
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(errorBody);
    }
}