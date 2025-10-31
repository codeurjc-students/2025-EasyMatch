package es.codeurjc.dto;

import java.util.Collection;
import java.util.List;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.codeurjc.model.Club;
import es.codeurjc.model.PriceRange;
import es.codeurjc.model.Schedule;


@Mapper(componentModel = "spring")
public interface ClubMapper {

    @Mapping(target = "schedule", expression = "java(toScheduleDTO(club.getSchedule()))")
    @Mapping(target = "priceRange", expression = "java(toPriceRangeDTO(club.getPriceRange()))")
    ClubDTO toDTO(Club club);

    List<ClubDTO> toDTOs(Collection<Club> clubs);

    Club toDomain(ClubDTO clubDTO);


    default ScheduleDTO toScheduleDTO(Schedule schedule) {
        if (schedule == null) return null;
        return new ScheduleDTO(schedule.getOpeningTime(), schedule.getClosingTime());
    }

    default PriceRangeDTO toPriceRangeDTO(PriceRange priceRange) {
        if (priceRange == null) return null;
        return new PriceRangeDTO(priceRange.getMinPrice(), priceRange.getMaxPrice(),priceRange.getUnit());
    }
}
