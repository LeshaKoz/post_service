package faang.school.postservice.messages;

import org.springframework.stereotype.Component;

@Component
public class ValidationMessages {
    public static final String validationKeyNotEmpty = "Key can't be empty";
    public static final String validationKeyMaxLength = "Length of key can't be more than 50 symbols";

    public static final String validationNameNotEmpty = "Name can't be empty";
    public static final String validationNameMaxLength = "Length of name can't be more than 150 symbols";

    public static final String validationTypeNotEmpty = "Type of file can't be empty";
    public static final String validationTypeMaxLength = "Length of type can't be more than 50 symbols";

    public static final String validationPostIdNotNull = "Post ID can't be null";
}
