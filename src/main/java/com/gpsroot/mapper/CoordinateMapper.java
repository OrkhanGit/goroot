package com.gpsroot.mapper;

import com.gpsroot.model.dto.CoordinateDto;
import com.gpsroot.model.entity.Coordinate;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CoordinateMapper {

    Coordinate toCoordinate(CoordinateDto coordinateDto);
    CoordinateDto toCoordinateDto(Coordinate coordinate);

}
