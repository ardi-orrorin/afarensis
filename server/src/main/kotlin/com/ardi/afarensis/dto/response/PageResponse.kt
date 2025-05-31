package com.ardi.afarensis.dto.response

class PageResponse<T>(
    val page: Int,
    val size: Int,
    val total: Long,
    val totalPages: Int,
    val isFirst: Boolean,
    val isLast: Boolean,
    val hasNext: Boolean,
    val hasPrevious: Boolean,
    val data: List<T>,
) {
    override fun toString(): String {
        return "PageResponse(page=$page, size=$size, total=$total, totalPages=$totalPages, isFirst=$isFirst, isLast=$isLast, hasNext=$hasNext, hasPrevious=$hasPrevious, data=$data)"
    }
}