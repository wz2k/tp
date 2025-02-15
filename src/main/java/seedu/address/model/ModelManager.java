package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataConversionException;
import seedu.address.model.pair.Pair;
import seedu.address.model.person.Elderly;
import seedu.address.model.person.Volunteer;
import seedu.address.model.person.information.Nric;
import seedu.address.storage.Storage;

/**
 * Represents the in-memory model of the FriendlyLink data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);
    private final FriendlyLink friendlyLink;
    private final UserPrefs userPrefs;
    private final FilteredList<Elderly> filteredElderly;
    private final FilteredList<Volunteer> filteredVolunteers;
    private final FilteredList<Pair> filteredPairs;

    /**
     * Constructs a {@code ModelManager} with the data from {@code Storage} and {@code userPrefs}. <br>
     * An empty application will be used instead if errors occur when reading {@code storage}.
     *
     * @param storage Storage to retrieve data from.
     * @param userPrefs User preferences.
     */
    public ModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(storage, userPrefs);
        FriendlyLink temporaryFriendlyLink = new FriendlyLink();
        try {
            temporaryFriendlyLink = storage.read();
        } catch (DataConversionException e) {
            logger.warning("Data file not in the correct format. Will be starting with an empty FriendlyLink");
        } catch (IOException e) {
            logger.warning("Problem while reading from the file. Will be starting with an empty FriendlyLink");
        }
        friendlyLink = temporaryFriendlyLink;
        logger.fine("Initializing with FriendlyLink: " + friendlyLink + " and user prefs " + userPrefs);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredElderly = new FilteredList<>(friendlyLink.getElderlyList());
        filteredVolunteers = new FilteredList<>(friendlyLink.getVolunteerList());
        filteredPairs = new FilteredList<>(friendlyLink.getPairList());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    //=========== FriendlyLink ================================================================================

    @Override
    public void setFriendlyLink(FriendlyLink friendlyLink) {
        this.friendlyLink.resetFriendlyLinkData(friendlyLink);
    }

    @Override
    public FriendlyLink getFriendlyLink() {
        return friendlyLink;
    }

    //=========== FriendlyLink Elderly  ======================================================================

    @Override
    public Path getElderlyFilePath() {
        return userPrefs.getElderlyFilePath();
    }

    @Override
    public void setElderlyFilePath(Path elderlyFilePath) {
        requireNonNull(elderlyFilePath);
        userPrefs.setElderlyFilePath(elderlyFilePath);
    }

    public Elderly getElderly(Nric nric) {
        requireNonNull(nric);
        return friendlyLink.getElderly(nric);
    }

    @Override
    public boolean hasElderly(Nric nric) {
        requireNonNull(nric);
        return friendlyLink.hasElderly(nric);
    }

    @Override
    public void deleteElderly(Elderly target) {
        friendlyLink.removeElderly(target);
        refreshAllFilteredLists();
    }

    @Override
    public void addElderly(Elderly elderly) {
        friendlyLink.addElderly(elderly);
        refreshAllFilteredLists();
    }

    @Override
    public void setElderly(Elderly target, Elderly editedElderly) {
        requireAllNonNull(target, editedElderly);
        friendlyLink.setElderly(target, editedElderly);
    }

    //=========== FriendlyLink Volunteers ======================================================================

    @Override
    public Path getVolunteerFilePath() {
        return userPrefs.getVolunteerFilePath();
    }

    @Override
    public void setVolunteerFilePath(Path volunteerFilePath) {
        requireNonNull(volunteerFilePath);
        userPrefs.setVolunteerFilePath(volunteerFilePath);
    }


    @Override
    public Volunteer getVolunteer(Nric nric) {
        requireNonNull(nric);
        return friendlyLink.getVolunteer(nric);
    }

    @Override
    public boolean hasVolunteer(Nric nric) {
        requireNonNull(nric);
        return friendlyLink.hasVolunteer(nric);
    }

    @Override
    public void deleteVolunteer(Volunteer target) {
        friendlyLink.removeVolunteer(target);
        refreshAllFilteredLists();
    }

    @Override
    public void addVolunteer(Volunteer volunteer) {
        friendlyLink.addVolunteer(volunteer);
        refreshAllFilteredLists();
    }

    @Override
    public void setVolunteer(Volunteer target, Volunteer editedVolunteer) {
        requireAllNonNull(target, editedVolunteer);
        friendlyLink.setVolunteer(target, editedVolunteer);
    }

    //=========== FriendlyLink Pairs ======================================================================

    @Override
    public Path getPairFilePath() {
        return userPrefs.getPairFilePath();
    }

    @Override
    public void setPairFilePath(Path friendlyLinkFilePath) {
        requireNonNull(friendlyLinkFilePath);
        userPrefs.setPairFilePath(friendlyLinkFilePath);
    }

    @Override
    public boolean hasPair(Pair pair) {
        requireNonNull(pair);
        return friendlyLink.hasPair(pair);
    }

    @Override
    public Pair addPair(Nric elderlyNric, Nric volunteerNric) {
        Pair pair = friendlyLink.addPair(elderlyNric, volunteerNric);
        refreshAllFilteredLists();
        return pair;
    }

    @Override
    public void addPair(Pair pair) {
        friendlyLink.addPair(pair);
        refreshAllFilteredLists();
    }

    @Override
    public void deletePair(Nric elderlyNric, Nric volunteerNric) {
        friendlyLink.removePair(elderlyNric, volunteerNric);
        refreshAllFilteredLists();
    }

    @Override
    public void setPair(Pair target, Pair editedPair) {
        requireAllNonNull(target, editedPair);
        friendlyLink.setPair(target, editedPair);
    }

    //=========== Filtered Elderly List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Elderly} backed by the internal list of
     * {@code versionedFriendlyLink}
     */
    @Override
    public ObservableList<Elderly> getFilteredElderlyList() {
        return filteredElderly;
    }

    @Override
    public void updateFilteredElderlyList(Predicate<Elderly> predicate) {
        requireNonNull(predicate);
        filteredElderly.setPredicate(predicate);
    }

    //=========== Filtered Volunteer List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Volunteer} backed by the internal list of
     * {@code versionedFriendlyLink}
     */
    @Override
    public ObservableList<Volunteer> getFilteredVolunteerList() {
        return filteredVolunteers;
    }

    @Override
    public void updateFilteredVolunteerList(Predicate<Volunteer> predicate) {
        requireNonNull(predicate);
        filteredVolunteers.setPredicate(predicate);
    }

    //=========== Filtered Pair List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Pair} backed by the internal list of
     * {@code versionedFriendlyLink}
     */
    @Override
    public ObservableList<Pair> getFilteredPairList() {
        return filteredPairs;
    }

    @Override
    public void updateFilteredPairList(Predicate<Pair> predicate) {
        requireNonNull(predicate);
        filteredPairs.setPredicate(predicate);
    }

    //=========== Others ==================================================================================

    @Override
    public boolean check(Elderly elderly, BiFunction<Elderly, Volunteer, Boolean> predicate) {
        return friendlyLink.check(elderly, predicate);
    }

    @Override
    public boolean check(Volunteer volunteer, BiFunction<Elderly, Volunteer, Boolean> predicate) {
        return friendlyLink.check(volunteer, predicate);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void refreshAllFilteredLists() {
        updateFilteredElderlyList((Predicate<Elderly>) PREDICATE_SHOW_ALL);
        updateFilteredVolunteerList((Predicate<Volunteer>) PREDICATE_SHOW_ALL);
        updateFilteredPairList((Predicate<Pair>) PREDICATE_SHOW_ALL);
    }

    @Override
    public void updateAllFilteredLists(Predicate<Elderly> elderlyPredicate,
            Predicate<Volunteer> volunteerPredicate, Predicate<Pair> pairPredicate) {
        updateFilteredElderlyList(elderlyPredicate);
        updateFilteredVolunteerList(volunteerPredicate);
        updateFilteredPairList(pairPredicate);
    }

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof ModelManager)) {
            return false;
        }

        // state check
        ModelManager other = (ModelManager) obj;
        return friendlyLink.equals(other.friendlyLink)
                && userPrefs.equals(other.userPrefs)
                && filteredElderly.equals(other.filteredElderly)
                && filteredVolunteers.equals(other.filteredVolunteers)
                && filteredPairs.equals(other.filteredPairs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(friendlyLink, userPrefs, filteredElderly,
                filteredVolunteers, filteredPairs);
    }
}
