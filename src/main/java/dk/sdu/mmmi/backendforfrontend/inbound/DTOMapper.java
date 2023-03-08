package dk.sdu.mmmi.backendforfrontend.inbound;

import dk.sdu.mmmi.backendforfrontend.service.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DTOMapper{

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    @Mapping(target = "company", ignore = true)
    JobDTO toJobDTO(Job job);

    ApplicationDTO toApplicationDTO(Application application);

    UserDTO toUserDTO(User user);
}
