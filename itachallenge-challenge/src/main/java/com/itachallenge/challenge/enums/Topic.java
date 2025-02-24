package com.itachallenge.challenge.enums;

public enum Topic {
    ALL("All"),
    COMPONENTS("Components"),
    USE_STATE_USE_EFFECT("useState & useEffect"),
    EVENTS("Events"),
    CONDITIONAL_RENDERING("Conditional Rendering"),
    LISTS("Lists"),
    STYLES("Styles"),
    DEBUGGING("Debugging"),
    REACT_ROUTER("React Router"),
    UNKNOWN("Unknown");

    private final String displayName;

    Topic(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Topic fromDisplayName(String displayName) {
        for (Topic topic : Topic.values()) {
            if (topic.getDisplayName().equalsIgnoreCase(displayName)) {
                return topic;
            }
        }
        throw new IllegalArgumentException("No enum constant with display name: " + displayName);
    }

    public static Topic getTopicFromString(String topicString) {
        try {
            return Topic.valueOf(topicString);
        } catch (IllegalArgumentException e) {
            return Topic.UNKNOWN;
        }
    }


}

