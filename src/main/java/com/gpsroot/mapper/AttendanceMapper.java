package com.gpsroot.mapper;

import com.gpsroot.qr.AttendanceLog;
import com.gpsroot.qr.ViewUsersDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {

    AttendanceLog toAttendanceLog(ViewUsersDto viewUsersDto);
    ViewUsersDto toViewUsersDto(AttendanceLog attendanceLog);

}
