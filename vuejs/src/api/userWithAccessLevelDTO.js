import {AbstractDTO} from "@/api/abstractDTO";
import {AccessLevelDTO} from "@/api/accessLevelDTO";

export class UserWithAccessLevelDTO extends AbstractDTO {
    /**
     * @param {number} id
     * @param {number} version
     * @param {string} login
     * @param {string} email
     * @param {boolean} isActive
     * @param {boolean} isVerified
     * @param {AccessLevelDTO[]} accessLevels
     */
    constructor(id, version, login, email, isActive, isVerified, accessLevels) {
        super(id, version);
        this.login = login;
        this.email = email;
        this.isActive = isActive;
        this.isVerified = isVerified;
        this.accessLevels = accessLevels;
    }

    static fromResponse(userData) {
        let accesLevels = userData.accessLevels.map((accesLevelData) => {
            return AccessLevelDTO.fromResponse(accesLevelData)
        })
        return new UserWithAccessLevelDTO(
            userData.id,
            userData.version,
            userData.login,
            userData.email,
            userData.active,
            userData.verified,
            accesLevels);
    }
}