package domain.world.util;

/**
* Enum that indicates response for updatePosition request
*/
public enum UpdateResponse {
    SUCCESS, //position was updated successfully
    FAILED_OUTSIDE_WORLD, //robot will go outside world limits if allowed, so it failed to update the position
    FAILED_OBSTRUCTED, //robot obstructed by at least one obstacle, thus cannot proceed.
}
