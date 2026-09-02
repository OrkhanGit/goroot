package com.gpsroot.walk.mapper;

import com.gpsroot.walk.model.BikeDto;
import com.gpsroot.walk.model.ViewWalkDto;
import com.gpsroot.walk.model.WalkSession;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WalkSessionMapper {

    WalkSession toWalkSession(ViewWalkDto viewwalkDto);
    WalkSession toWalkSession(BikeDto bikeDto);

    ViewWalkDto toviewWalkDto(WalkSession walkSession);
    BikeDto toBikeDto(WalkSession walkSession);

}
