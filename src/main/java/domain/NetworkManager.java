package domain;

import java.util.*;

public class NetworkManager {
    public static int countCommunities(List<User> users, List<Friendship> friendships) {
        List<Set<User>> usersCommunities=getCommunities(users,friendships);
        int communities=usersCommunities.size();
        return communities;
    }

    public static List<Set<User>> getCommunities(List<User> users, List<Friendship> friendships) {
        Map<Long, User> userById = new HashMap<>();
        for (User u : users) userById.put(u.getId(), u);

        Map<User, List<User>> adj = new HashMap<>();
        for (User u : users) adj.put(u, new ArrayList<>());

        for (Friendship f : friendships) {
            User u1 = userById.get(f.getUser1());
            User u2 = userById.get(f.getUser2());
            if (u1 != null && u2 != null) {
                adj.get(u1).add(u2);
                adj.get(u2).add(u1);
            }
        }

        Set<User> visited = new HashSet<>();
        List<Set<User>> communities = new ArrayList<>();

        for (User u : users) {
            if (!visited.contains(u)) {
                Set<User> community = new HashSet<>();
                dfs(u, adj, visited, community);
                communities.add(community);
            }
        }
        return communities;
    }

    private static void dfs(User user, Map<User, List<User>> adj, Set<User> visited, Set<User> community) {
        visited.add(user);
        community.add(user);
        for (User neighbor : adj.get(user)) {
            if (!visited.contains(neighbor)) {
                dfs(neighbor, adj, visited, community);
            }
        }
    }

    public static Set<User> mostSociableCommunity(List<User> users, List<Friendship> friendships) {
        List<Set<User>> communities = getCommunities(users, friendships);
        Map<Long, User> userById = new HashMap<>();
        for (User u : users) userById.put(u.getId(), u);

        Map<User, List<User>> adj = new HashMap<>();
        for (User u : users) adj.put(u, new ArrayList<>());
        for (Friendship f : friendships) {
            User u1 = userById.get(f.getUser1());
            User u2 = userById.get(f.getUser2());
            if (u1 != null && u2 != null) {
                adj.get(u1).add(u2);
                adj.get(u2).add(u1);
            }
        }

        Set<User> bestCommunity = null;
        int maxDiameter = -1;

        for (Set<User> community : communities) {
            int diam = getDiameter(community, adj);
            if (diam > maxDiameter) {
                maxDiameter = diam;
                bestCommunity = community;
            }
        }
        return bestCommunity;
    }

    private static int getDiameter(Set<User> community, Map<User, List<User>> adj) {
        if (community.isEmpty()) return 0;
        User start = community.iterator().next();
        User farthest = bfsFarthest(start, community, adj).user;
        BFSResult result = bfsFarthest(farthest, community, adj);
        return result.distance;
    }

    private static BFSResult bfsFarthest(User start, Set<User> community, Map<User, List<User>> adj) {
        Map<User, Integer> dist = new HashMap<>();
        Queue<User> q = new LinkedList<>();
        dist.put(start, 0);
        q.add(start);

        User farthest = start;
        int maxDist = 0;

        while (!q.isEmpty()) {
            User current = q.poll();
            for (User neighbor : adj.get(current)) {
                if (community.contains(neighbor) && !dist.containsKey(neighbor)) {
                    dist.put(neighbor, dist.get(current) + 1);
                    q.add(neighbor);
                    if (dist.get(neighbor) > maxDist) {
                        maxDist = dist.get(neighbor);
                        farthest = neighbor;
                    }
                }
            }
        }
        return new BFSResult(farthest, maxDist);
    }

    private static class BFSResult {
        User user;
        int distance;
        BFSResult(User u, int d) {
            this.user = u;
            this.distance = d;
        }
    }
}

