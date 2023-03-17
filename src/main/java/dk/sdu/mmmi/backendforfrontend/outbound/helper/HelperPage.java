package dk.sdu.mmmi.backendforfrontend.outbound.helper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.sdu.mmmi.backendforfrontend.service.model.Job;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

public class HelperPage extends PageImpl<Job> {

    @JsonCreator
    public HelperPage(@JsonProperty("content") List<Job> content,
                      @JsonProperty("number") int number,
                      @JsonProperty("size") int size,
                      @JsonProperty("totalElements") Long totalElements) {
        super(content, PageRequest.of(number, size, Sort.unsorted()), totalElements);
    }
}