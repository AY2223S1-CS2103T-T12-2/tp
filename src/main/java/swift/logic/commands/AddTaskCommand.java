package swift.logic.commands;

import static swift.commons.util.CollectionUtil.requireAllNonNull;
import static swift.logic.parser.CliSyntax.PREFIX_CONTACT;
import static swift.logic.parser.CliSyntax.PREFIX_NAME;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import swift.commons.core.index.Index;
import swift.logic.commands.exceptions.CommandException;
import swift.logic.parser.Prefix;
import swift.model.Model;
import swift.model.task.Task;

/**
 * Adds a task to the address book.
 */
public class AddTaskCommand extends Command {

    public static final String COMMAND_WORD = "add_task";
    public static final ArrayList<Prefix> ARGUMENT_PREFIXES = new ArrayList<>(List.of(PREFIX_NAME, PREFIX_CONTACT));

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds a task to the task list.\n"
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + "[" + PREFIX_CONTACT + "CONTACT]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "CS2103T Tutorial "
            + PREFIX_CONTACT + "1 "
            + PREFIX_CONTACT + "2";

    public static final String MESSAGE_SUCCESS = "New task added: %1$s";
    public static final String MESSAGE_DUPLICATE_TASK = "This task already exists in the address book";

    private final Task toAdd;
    private final Collection<Index> contactIndices;

    /**
     * Creates an AddTaskCommand to add the specified {@code Task}
     *
     * @param task Task to be added.
     */
    public AddTaskCommand(Task task, Collection<Index> indices) {
        requireAllNonNull(task, indices);
        toAdd = task;
        contactIndices = indices;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireAllNonNull(model);

        if (model.hasTask(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_TASK);
        }
        model.addTask(toAdd);
        for (Index index : contactIndices) {
            model.addBridge(model.getFilteredPersonList().get(index.getZeroBased()), toAdd);
        }

        model.hotUpdateAssociatedContacts();

        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AddTaskCommand // instanceof handles nulls
                && toAdd.equals(((AddTaskCommand) other).toAdd));
    }
}
