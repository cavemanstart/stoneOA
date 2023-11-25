import request from '@/utils/request'

const commonURL = 'admin/system/sysRole'
export default {
    //查询所有角色
    getAll(){
        return request({
            url: `${commonURL}/allRoles`,
            method: 'get',
        })
    },
    //角色列表--条件分页查询
    getPageList(current, size, sysName) {
        return request({
            url: `${commonURL}/${current}/${size}`,
            method: 'get',
            params: sysName
        })
    },
    //角色删除
    removeRoleById(id) {
        return request({
            url: `${commonURL}/removeRole/${id}`,
            method: 'delete'
        })
    },
    //角色添加
    saveRole(role) {
        return request({
            url: `${commonURL}/addRole`,
            method: 'post',
            data: role
        })
    },
    //根据id查询
    getRoleById(id) {
        return request({
            url: `${commonURL}/getRoleById/${id}`,
            method: 'get',
        })
    },
    //修改  
    updateRoleById(role) {
        return request({
            url: `${commonURL}/updateRole`,
            method: 'put',
            data: role
        })
    },
    //批量删除
    batchRemove(idList) {
        return request({
            url: `${commonURL}/removeBatchRole`,
            method: 'delete',
            data: idList
        })
    },
    getRoles(userId) {
        return request({
            url: `${commonURL}/getRoleListByUserId/${userId}`,
            method: 'get'
        })
    },

    assignRoles(assginRoleVo) {
        return request({
            url: `${commonURL}/assign`,
            method: 'post',
            data: assginRoleVo
        })
    }
}
