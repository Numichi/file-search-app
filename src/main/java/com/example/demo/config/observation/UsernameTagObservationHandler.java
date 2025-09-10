package com.example.demo.config.observation;

import com.example.demo.exceptions.base.HttpUnauthorizedException;
import com.example.demo.service.AuthenticationService;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import io.opentelemetry.api.trace.Span;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsernameTagObservationHandler implements ObservationHandler<Observation.Context> {

    private final AuthenticationService authenticationService;

    @Override
    public void onStart(@NonNull Observation.Context context) {
        try {
            Span.current().setAttribute("user-id", authenticationService.getUsername());
        } catch (HttpUnauthorizedException e) {
            Span.current().setAttribute("user-id", "n/a");
        }
    }

    @Override
    public boolean supportsContext(@NonNull Observation.Context context) {
        return true;
    }
}
