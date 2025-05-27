package com.ardi.afarensis.service

import com.ardi.afarensis.dto.request.RequestPage
import com.ardi.afarensis.repository.SystemLogRepository
import com.ardi.afarensis.util.toResponse
import com.ardi.afarensis.util.toZeroBasedPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class SystemLogService(
    private val systemLogRepository: SystemLogRepository
) {

    suspend fun findAll(req: RequestPage) = withContext(Dispatchers.IO) {
        val pageable = PageRequest.of(req.page.toZeroBasedPage(), req.size, Sort.by(req.sortBy, req.sortDirection));

        val page = systemLogRepository.findAll(pageable)

        val list = page.content.map { it.toDto() }.toList()

        page.toResponse(list)
    }

    suspend fun updateIsReadById(id: Long) = withContext(Dispatchers.IO) {
        systemLogRepository.updateIsReadById(id)
    }

}