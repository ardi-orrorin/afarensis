package com.ardi.afarensis.service

import com.ardi.afarensis.dto.request.RequestPage
import com.ardi.afarensis.repository.SystemLogRepository
import com.ardi.afarensis.util.toResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class SystemLogService(
    private val systemLogRepository: SystemLogRepository
) {

    suspend fun findAll(req: RequestPage) = withContext(Dispatchers.IO) {

        val page = systemLogRepository.findAll(req.toPageRequest())

        val list = page.content.map { it.toDto() }.toList()

        page.toResponse(list)
    }

    suspend fun updateIsReadById(id: Long) = withContext(Dispatchers.IO) {
        systemLogRepository.updateIsReadById(id)
    }

}