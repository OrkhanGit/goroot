package com.gpsroot.walk.mapper;

import com.gpsroot.walk.model.ViewBikeDto;
import com.gpsroot.walk.model.ViewWalkDto;
import com.gpsroot.walk.model.WalkSession;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WalkSessionMapper {

    WalkSession toWalkSession(ViewWalkDto viewwalkDto);
    WalkSession toWalkSession(ViewBikeDto bikeDto);

    ViewWalkDto toviewWalkDto(WalkSession walkSession);
    ViewBikeDto toviewBikeDto(WalkSession walkSession);



}
