import request from '@/utils/request'

const api_name = '/admin/system/sysUser'

export default {

    getPageList(page, limit, searchObj) {
        return request({
            url: `${api_name}/${page}/${limit}`,
            method: 'get',
            params: searchObj // url查询字符串或表单键值对
        })
    },
    getById(id) {
        return request({
            url: `${api_name}/getUserById/${id}`,
            method: 'get'
        })
    },

    save(role) {
        return request({
            url: `${api_name}/addUser`,
            method: 'post',
            data: role
        })
    },

    updateById(role) {
        return request({
            url: `${api_name}/updateUser`,
            method: 'put',
            data: role
        })
    },
    removeById(id) {
        return request({
            url: `${api_name}/removeUserById/${id}`,
            method: 'delete'
        })
    },
    updateStatus(id, status) {
        return request({
            url: `${api_name}/modifyUserStatus/${id}/${status}`,
            method: 'get'
        })
    }
}