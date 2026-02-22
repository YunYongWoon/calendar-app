package com.calendar.domain.exception

class GroupNotFoundException(message: String = "그룹을 찾을 수 없습니다.") :
    CalendarException(errorCode = "GROUP_NOT_FOUND", message = message)

class GroupMemberNotFoundException(message: String = "그룹 멤버를 찾을 수 없습니다.") :
    CalendarException(errorCode = "GROUP_MEMBER_NOT_FOUND", message = message)

class MaxGroupLimitExceededException(message: String = "최대 그룹 가입 수를 초과하였습니다.") :
    CalendarException(errorCode = "MAX_GROUP_LIMIT_EXCEEDED", message = message)

class MaxMemberLimitExceededException(message: String = "그룹의 최대 인원을 초과하였습니다.") :
    CalendarException(errorCode = "MAX_MEMBER_LIMIT_EXCEEDED", message = message)

class AlreadyGroupMemberException(message: String = "이미 그룹에 가입되어 있습니다.") :
    CalendarException(errorCode = "ALREADY_GROUP_MEMBER", message = message)

class InvalidInviteCodeException(message: String = "유효하지 않은 초대 코드입니다.") :
    CalendarException(errorCode = "INVALID_INVITE_CODE", message = message)

class InsufficientPermissionException(message: String = "권한이 부족합니다.") :
    CalendarException(errorCode = "INSUFFICIENT_PERMISSION", message = message)

class OwnerCannotLeaveException(message: String = "그룹 소유자는 그룹을 나갈 수 없습니다. 소유권을 위임해주세요.") :
    CalendarException(errorCode = "OWNER_CANNOT_LEAVE", message = message)

class CannotRemoveOwnerException(message: String = "그룹 소유자는 내보낼 수 없습니다.") :
    CalendarException(errorCode = "CANNOT_REMOVE_OWNER", message = message)
