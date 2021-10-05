import {api} from "@/api/index";
import eventBus from "@/event-bus";
import {UnavailabilityDTO} from "./unavailabilityDTO";

// const managerUrl = "manager/";
const entertainerUrl = "moo/entertainer/";

/**
 *
 * @returns {Promise<UserWithAccessLevelDTO[]>}
 */


async function getEntertainerOwnUnavailabilities() {
    return api.get(entertainerUrl + "unavailability/my").then(response => {
        let unavailability = []
        response.data.forEach(record => {
            unavailability.push(UnavailabilityDTO.fromResponse(record))
        })
        console.log(unavailability)
        return unavailability
    }).catch(function (error) {
        eventBus.$emit('alert', {key: error.response.data.key, variant: 'danger'});
        console.log(error.response.data);
        console.log(error.response.status);
        console.log(error.response.headers);
    }, reason => {
        return reason
    })
}

async function updateActivenessOfUnavailability(id, status) {
    return api.put(entertainerUrl + "unavailability/" + id + "/" + status).then(resp => {
        return resp
    })
        .catch(function (error) {
            eventBus.$emit('alert', {key: error.response.data.key, variant: 'danger'});
            console.log(error.response.data);
            console.log(error.response.status);
            console.log(error.response.headers);
        }, reason => {
            return reason
        })
}

async function addUnavailability(dateFrom, dateTo, describtion) {
    return await api.post(entertainerUrl + "unavailability", {
        "description": describtion,
        "dateTimeFrom": dateFrom,
        "dateTimeTo": dateTo
    }).then(response => {
        console.log(response)
        return response.data
    }).catch(function (error) {
        eventBus.$emit('alert', {key: error.response.data.key, variant: 'danger'});
        console.log(error.response.data);
        console.log(error.response.status);
        console.log(error.response.headers);
    }, reason => {
        return reason
    })
}

async function addNewOffer(active, title, description, validFrom, validTo) {
    return await api.post("/moo/offer/create", {
        "active": active,
        "description": description,
        "title": title,
        "validFrom": validFrom,
        "validTo": validTo
    }).then(response => {
        console.log(response)
        return response.data
    }).catch(function (error) {
        eventBus.$emit('alert', {key: error.response.data.key, variant: 'danger'});
        console.log(error.response.data);
        console.log(error.response.status);
        console.log(error.response.headers);
    }, reason => {
        return reason
    })
}

async function deactivateOffer(id) {
    return await api.put("/moo/offer/" + id + "/false")
        .then(response => {
            console.log(response)
            return response.data
        }).catch(function (error) {
            eventBus.$emit('alert', {key: error.response.data.key, variant: 'danger'});
            console.log(error.response.data);
            console.log(error.response.status);
            console.log(error.response.headers);
        }, reason => {
            return reason
        })

}

export const apiEntertainer = {
    getEntertainerOwnUnavailabilities,
    updateActivenessOfUnavailability,
    addUnavailability,
    addNewOffer,
    deactivateOffer

}