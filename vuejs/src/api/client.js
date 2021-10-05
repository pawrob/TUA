import {api} from "@/api/index";

const clientUrl = "clients/";

/**
 *
 * @returns
 */
function deleteAccount(login, password) {
    return api.delete(clientUrl + `user/${encodeURI(login)}`, {
        data: {
            password: password
        }
    }).catch(() => {
        return false;
    }).then(response => {
        return response;
    })
}

export const apiClient = {
    deleteAccount,
}