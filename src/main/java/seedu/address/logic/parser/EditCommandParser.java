package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_TO_EDIT_NRIC;
import static seedu.address.commons.core.Messages.MESSAGE_NOT_EDITED;
import static seedu.address.logic.commands.EditCommand.MESSAGE_USAGE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AGE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AVAILABILITY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MEDICAL_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NRIC;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REGION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_RISK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.model.person.information.Nric.MESSAGE_CONSTRAINTS;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.util.EditDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.information.Nric;

/**
 * Parses input arguments for editing.
 */
public class EditCommandParser implements Parser <EditCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     *
     * @param args Arguments.
     * @return {@code EditCommand} for execution.
     * @throws ParseException If the user input does not conform the expected format.
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                        PREFIX_NRIC, PREFIX_AGE, PREFIX_REGION, PREFIX_AVAILABILITY,
                        PREFIX_RISK, PREFIX_TAG, PREFIX_MEDICAL_TAG);

        Nric nric = checkPreamble(argMultimap.getPreamble());

        EditDescriptor editDescriptor = new EditDescriptor();
        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editDescriptor.setName(
                    ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get()));
        }
        if (argMultimap.getValue(PREFIX_PHONE).isPresent()) {
            editDescriptor.setPhone(
                    ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get()));
        }
        if (argMultimap.getValue(PREFIX_EMAIL).isPresent()) {
            editDescriptor.setEmail(
                    ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get()));
        }
        if (argMultimap.getValue(PREFIX_ADDRESS).isPresent()) {
            editDescriptor.setAddress(
                    ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get()));
        }
        if (argMultimap.getValue(PREFIX_NRIC).isPresent()) {
            editDescriptor.setNric(
                    ParserUtil.parseNric(argMultimap.getValue(PREFIX_NRIC).get()));
        }
        if (argMultimap.getValue(PREFIX_AGE).isPresent()) {
            editDescriptor.setAge(
                    ParserUtil.parseAge(argMultimap.getValue(PREFIX_AGE).get()));
        }
        if (argMultimap.getValue(PREFIX_REGION).isPresent()) {
            editDescriptor.setRegion(
                    ParserUtil.parseRegion(argMultimap.getValue(PREFIX_REGION).get()));
        }
        if (argMultimap.getValue(PREFIX_RISK).isPresent()) {
            editDescriptor.setRiskLevel(
                    ParserUtil.parseRiskLevel(argMultimap.getValue(PREFIX_RISK).get()));
        }
        parseRepeatableArgumentsForEdit(argMultimap.getAllValues(PREFIX_TAG), ParserUtil::parseTags)
                .ifPresent(editDescriptor::setTags);
        parseRepeatableArgumentsForEdit(argMultimap.getAllValues(PREFIX_MEDICAL_TAG), ParserUtil::parseMedicalTags)
                .ifPresent(editDescriptor::setMedicalTags);
        parseRepeatableArgumentsForEdit(argMultimap.getAllValues(PREFIX_AVAILABILITY), ParserUtil::parseDateRanges)
                .ifPresent(editDescriptor::setAvailableDates);

        if (!editDescriptor.isAnyFieldEdited()) {
            throw new ParseException(MESSAGE_NOT_EDITED);
        }

        return new EditCommand(nric, editDescriptor);
    }

    private Nric checkPreamble(String preamble) throws ParseException {
        if (preamble.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
        }
        if (!Nric.isValidNric(preamble)) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_TO_EDIT_NRIC, MESSAGE_CONSTRAINTS));
        }
        return new Nric(preamble);
    }

    /**
     * Parses {@code Collection<String> args} into a Set if {@code args} is non-empty.
     * If {@code args} contain only one element which is an empty string, it will be parsed into an empty Set.
     * This is used for parsing Tags, AvailableDates, and MedicalQualificationTags.
     *
     * @param args Arguments.
     * @return {@code Optional} of a set of the specified type.
     * @throws ParseException If any of the arguments is not valid.
     */
    public static <T> Optional<Set<T>> parseRepeatableArgumentsForEdit(
            Collection<String> args,
            ParserUtil.ArgumentParser<Collection<String>, Set<T>> parser) throws ParseException {
        assert args != null;

        if (args.isEmpty()) {
            return Optional.empty();
        }
        Collection<String> tagSet = args.size() == 1 && args.contains("") ? Collections.emptySet() : args;
        return Optional.of(parser.apply(tagSet));
    }

    /**
     * Validates the given ArgumentMultimap by checking that it fulfils certain criteria.
     *
     * @param map the ArgumentMultimap to be validated.
     * @return true if the ArgumentMultimap is valid, false otherwise.
     */
    public static boolean validate(ArgumentMultimap map) {
        return !(map.getArrayValue(PREFIX_NAME).orElse(List.of()).size() > 1
                || map.getArrayValue(PREFIX_PHONE).orElse(List.of()).size() > 1
                || map.getArrayValue(PREFIX_EMAIL).orElse(List.of()).size() > 1
                || map.getArrayValue(PREFIX_ADDRESS).orElse(List.of()).size() > 1
                || map.getArrayValue(PREFIX_AGE).orElse(List.of()).size() > 1
                || map.getArrayValue(PREFIX_REGION).orElse(List.of()).size() > 1
                || map.getArrayValue(PREFIX_TAG).orElse(List.of()).size() > 1
                || map.getArrayValue(PREFIX_NRIC).orElse(List.of()).size() > 1);
    }
}
