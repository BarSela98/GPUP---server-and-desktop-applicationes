package gpup.servlets;

import ODT.User;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/*
Adding and retrieving users is synchronized and in that manner - these actions are thread safe
Note that asking if a user exists (isUserExists) does not participate in the synchronization and it is the responsibility
of the user of this class to handle the synchronization of isUserExists with other methods here on it's own
 */
public class UserManager {

    private final Set<User> usersSet;
     public UserManager() {
        usersSet = new HashSet<>();
    }

    public synchronized void addUser(String username, String role) {
        usersSet.add(new User(username,role));
    }

    public synchronized void removeUser(String username) {
        usersSet.removeIf(user -> user.getName().equals(username));
    }

    public synchronized Set<User> getUsers() {
        return Collections.unmodifiableSet(usersSet);
    }

    public synchronized String getUserRole(String username) {
        for(User user: usersSet)
            if (user.getName().equals(username))
                return user.getRole();
        return null;
    }

    public boolean isUserExists(String username) {
        for(User user: usersSet)
            if (user.getName().equals(username))
                return true;
        return false;
    }

}
