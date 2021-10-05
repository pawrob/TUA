import {AbstractDTO} from "@/api/abstractDTO";

export class AccessLevelDTO extends AbstractDTO {
    /**
     * @param {number} id
     * @param {number} version
     * @param {string} accessLevel
     * @param {boolean} isActive
     */
    constructor(id, version, accessLevel, isActive) {
        super(id, version);
        this.accessLevel = accessLevel
        this.isActive = isActive
    }

    static fromResponse(accesLevelData) {
        return new AccessLevelDTO(
            accesLevelData.id,
            accesLevelData.version,
            accesLevelData.accessLevel,
            accesLevelData.active);
    }
}