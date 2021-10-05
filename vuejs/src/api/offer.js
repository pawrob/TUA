import {api} from "@/api/index";

const clientUrl = "moo/offer/";

/**
 *
 * @returns
 */
function editOffer(id, version, title, description, validFrom, validTo, offerAvailabilities) {
    return api.put(clientUrl + `${encodeURI(id)}`, {
        id: id,
        version: version,
        title: title,
        description: description,
        validFrom: validFrom,
        validTo: validTo,
        offerAvailabilities: offerAvailabilities
    }).catch(() => {
        return false;
    }).then(response => {
        return response;
    })
}

function getOffer(id) {
    return api.get(clientUrl + `${encodeURI(id)}`).catch(() => {
        return null;
    }).then(response => {
        return response.data;
    })
}

export const apiOffer = {
    editOffer,
    getOffer
}