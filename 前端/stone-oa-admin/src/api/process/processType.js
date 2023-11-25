import request from '@/utils/request'

const api_name = '/admin/process/processType'

export default {

  getPageList(page, limit) {
    return request({
      url: `${api_name}/getPages/${page}/${limit}`,
      method: 'get'
    })
  },
  getById(id) {
    return request({
      url: `${api_name}/getProcessType/${id}`,
      method: 'get'
    })
  },

  save(role) {
    return request({
      url: `${api_name}/save`,
      method: 'post',
      data: role
    })
  },

  updateById(role) {
    return request({
      url: `${api_name}/update`,
      method: 'put',
      data: role
    })
  },
  removeById(id) {
    return request({
      url: `${api_name}/remove/${id}`,
      method: 'delete'
    })
  },

  findAll() {
    return request({
      url: `${api_name}/findAll`,
      method: 'get'
    })
  }
}