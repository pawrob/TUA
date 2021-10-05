import {api} from "@/api/index";
import {UserWithAccessLevelDTO} from "@/api/userWithAccessLevelDTO";
import {OfferDTO} from "@/api/offerDTO"
import eventBus from "@/event-bus";

// const managerUrl = "manager/";
const userUrl = "user/";
const offerUrl = "moo/offer/";
const reservationUrl = "/moo/reservation";


/**
 *
 * @returns {Promise<UserWithAccessLevelDTO[]>}
 */
const getAllUsers = () => {
    return api.get(userUrl + 'all').then(response => {
        let userDatas = []
        response.data.forEach(userData => {
            userDatas.push(UserWithAccessLevelDTO.fromResponse(userData))
        })
        return userDatas
    }, reason => {
        return reason
    })
}

const getAllOffers = () => {
    return api.get(offerUrl + 'allActive').then(response => {
        let offersData = []
        response.data.forEach(offerData => {
            offersData.push(OfferDTO.fromResponse(offerData))
        })
        return offersData
    }, reason => {
        return reason
    })
}

const getFavoriteOffers = () => {
    return api.get(offerUrl + 'favorites').then(response => {
        let offersData = []
        response.data.forEach(offerData => {
            offersData.push(OfferDTO.fromResponse(offerData))
        })
        return offersData
    }, reason => {
        return reason
    })
}

function getUsersByPieceOfPersonalData(piece){
    return api.get(userUrl + 'getUsersByPieceOfPersonalData/?query='+piece).then(response => {
        let userDatas = []
        response.data.forEach(userData => {
            userDatas.push(UserWithAccessLevelDTO.fromResponse(userData))
        })
        return userDatas
    }, reason => {
        return reason
    })
}

function deactivateUser(id) {
    return api.put(userUrl + id +  '/deactivate').then(response => {
        eventBus.$emit('alert', {key:  'ACCOUNT_DEACTIVATED', variant: 'success'});
        console.log(response.status);
    }).catch(function (error) {
        eventBus.$emit('alert', {key: error.response.data.key, variant: 'danger'});
        console.log(error.response.data);
        console.log(error.response.status);
        console.log(error.response.headers);
    }, reason => {
        return reason
    })
}

function activateUser(id) {
    return api.put(userUrl + id +  '/activate').then(response => {
        eventBus.$emit('alert', {key:  'ACCOUNT_ACTIVATED', variant: 'success'});
        console.log(response.status);
    }).catch(function (error) {
        eventBus.$emit('alert', {key: error.response.data.key, variant: 'danger'});
        console.log(error.response.data);
        console.log(error.response.status);
        console.log(error.response.headers);
    }, reason => {
        return reason
    })
}

function requestChangeEmail(login, newEmail) {
    return api.get(`${userUrl}${encodeURI(login)}/requestChangeEmail?email=${encodeURI(newEmail)}`).then(response => {
        return (response.status === 200)
    }).catch(() => {
        return false;
    })
}

function changeEmail(id, token, email) {
    return api.get(`${userUrl}changeEmail?id=${encodeURI(id)}&token=${encodeURI(token)}&email=${encodeURI(email)}`).then(response => {
        return (response.status === 200)
    }).catch(() => {
        return false;
    })
}

async function changeAccessLevelStatusForEntertainer(accessLevelId, newStatus) {
    return api.put('entertainer/accessLevelChange/' + accessLevelId + "/" + newStatus).then(response => {
        console.log(response)
        if(newStatus) {
            eventBus.$emit('alert', {key: 'ACCESS_LEVEL_REACTIVATE', variant: 'success'});
        } else{
            eventBus.$emit('alert', {key: 'ACCESS_LEVEL_DEACTIVATE', variant: 'success'});
        }
    }).catch(function (error) {
        eventBus.$emit('alert', {key: error.response.data.key, variant: 'danger'});
        console.log(error.response.data);
        console.log(error.response.status);
        console.log(error.response.headers);
    }, reason => {
        return reason
    })
}

async function changeAccessLevelStatus(accessLevelId, newStatus) {
    return api.put('user/accessLevelChangeStatus/' + accessLevelId + "/" + newStatus).then(response => {
        console.log(response)
        if(newStatus) {
            eventBus.$emit('alert', {key: 'ACCESS_LEVEL_REACTIVATE', variant: 'success'});
        } else{
            eventBus.$emit('alert', {key: 'ACCESS_LEVEL_DEACTIVATE', variant: 'success'});
        }
    }).catch(function (error) {
        eventBus.$emit('alert', {key: error.response.data.key, variant: 'danger'});
        console.log(error.response.data);
        console.log(error.response.status);
        console.log(error.response.headers);
    }, reason => {
        return reason
    })
}

async function submitReservation(offerId, reservationFrom, reservationTo, offerVersion) {
    return api.post(reservationUrl,{
        reservationFrom: reservationFrom,
        reservationTo:  reservationTo,
        offer: offerId,
        offerVersion: offerVersion
    }).then(() => {
        eventBus.$emit('alert', {key: 'RESERVATION_CREATED', variant: 'success'})
    }).catch(() => {

    })
}

export const apiManager = {
    getAllUsers,
    getAllOffers,
    getUsersByPieceOfPersonalData,
    deactivateUser,
    activateUser,
    requestChangeEmail,
    changeEmail,
    changeAccessLevelStatusForEntertainer,
    changeAccessLevelStatus,
    submitReservation,
    getFavoriteOffers,
}