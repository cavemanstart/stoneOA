import request from '@/utils/request'

const api_name = '/admin/process'

export default {

  findProcessType() {
    return request({
      url: `${api_name}/findAllProcesses`,
      method: 'get'
    })
  },

  getProcessTemplate(processTemplateId) {
    return request({
      url: `${api_name}/getTemplateById/`+processTemplateId,
      method: 'get'
    })
  },

  startUp(processFormVo) {
    return request({
      url: `${api_name}/startUp`,
      method: 'post',
      data: processFormVo
    })
  },

  findPending(page, limit) {
    return request({
      url: `${api_name}/findBusiness/`+page+`/`+ limit,
      method: 'get'
    })
  },

  show(id) {
    return request({
      url: `${api_name}/showTaskInfo/`+id,
      method: 'get'
    })
  },

  approve(approvalVo) {
    return request({
      url: `${api_name}/approve`,
      method: 'post',
      data: approvalVo
    })
  },


  findProcessed(page, limit) {
    return request({
      url: `${api_name}/findProcessed/`+page+`/`+ limit,
      method: 'get'
    })
  },

  findStarted(page, limit) {
    return request({
      url: `${api_name}/findStarted/`+page+`/`+ limit,
      method: 'get'
    })
  },



}
