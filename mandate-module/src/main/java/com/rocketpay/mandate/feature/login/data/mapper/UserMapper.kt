package com.rocketpay.mandate.feature.login.data.mapper

import com.rocketpay.mandate.feature.login.data.entities.UserDto
import com.rocketpay.mandate.feature.login.domain.entities.User
import com.udharpay.kernel.kernelcommon.mapper.Mapper

internal class UserMapper : Mapper<UserDto, User> {
    override fun map(source: UserDto): User {
        return User(
            mobileNumber = source.mobileNumber,
            newUser = source.newUser,
            token = source.token,
            userId = source.userId,
            accountId = source.accountId
        )
    }
}
