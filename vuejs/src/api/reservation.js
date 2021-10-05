import {api} from "@/api/index";

const clientUrl = "moo/reservation/";

/**
 *
 * @returns
 */
function editReservation(id, version, reservationFrom, reservationTo) {
    return api.put(clientUrl + `${encodeURI(id)}`, {
        id: id,
        version: version,
        reservationFrom: reservationFrom,
        reservationTo: reservationTo
    }).catch(() => {
        return false;
    }).then(response => {
        return response;
    })
}

function getReservation(id) {
    return api.get(clientUrl + `${encodeURI(id)}`).catch(() => {
        return null;
    }).then(response => {
        return response.data;
    })
}

export const apiReservation = {
    editReservation,
    getReservation
}