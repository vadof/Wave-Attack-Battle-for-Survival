package com.wabs.server.database;

import com.wabs.global.gameEvents.GameEndEvent;
import com.wabs.server.database.entites.DbPlayer;
import com.wabs.server.database.entites.Match;
import com.wabs.server.database.entites.PlayerMatchStatistics;
import com.wabs.server.database.entites.PlayerStatistics;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class HibernateUtil {

    private static SessionFactory sessionFactory = null;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            Properties properties = new Properties();
            properties.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:8081/wabs");
            properties.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
            properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
            properties.setProperty("hibernate.connection.username", "user");
            properties.setProperty("hibernate.connection.password", "password");
            properties.setProperty("hibernate.current_session_context_class", "thread");
            properties.setProperty("hibernate.connection.autocommit", "false");
                sessionFactory = new Configuration()
                    .addProperties(properties)
                    .addAnnotatedClass(DbPlayer.class)
                    .addAnnotatedClass(Match.class)
                    .addAnnotatedClass(PlayerMatchStatistics.class)
                    .addAnnotatedClass(PlayerStatistics.class)
                    .buildSessionFactory();
        }
        return sessionFactory;
    }


    public static void saveStatistics(GameEndEvent gameEndEvent) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();

        Match match = new Match();
        match.setWin(gameEndEvent.win);

        List<PlayerMatchStatistics> stats = new ArrayList<>();
        List<DbPlayer> dbPlayerList = new ArrayList<>();

        for (String username : gameEndEvent.playerUsernames) {
            dbPlayerList.add(getPlayerByUsername(username));
        }

        for (int i = 0; i < gameEndEvent.playerUsernames.size(); i++) {
            PlayerMatchStatistics pms = new PlayerMatchStatistics();
            pms.setDeaths(gameEndEvent.playersDeath.get(i));
            pms.setKills(gameEndEvent.playersKills.get(i));
            pms.setDamageDealt(gameEndEvent.playersDamage.get(i));
            pms.setDamageReceived(gameEndEvent.playersDamageReceived.get(i));
            pms.setPlayer(dbPlayerList.get(i));
            stats.add(pms);
        }

        match.setPlayerMatchStats(stats);

        for (PlayerMatchStatistics pms : stats) {
            pms.setMatch(match);
            session.persist(pms);
        }

        for (int i = 0; i < dbPlayerList.size(); i++) {
            PlayerStatistics ps = dbPlayerList.get(i).getPlayerStatistics();
            ps.addGame(gameEndEvent.win);
            ps.addKills(gameEndEvent.playersKills.get(i));
            ps.addDamageDealt(gameEndEvent.playersDamage.get(i));
            ps.addDamageReceived(gameEndEvent.playersDamageReceived.get(i));
            ps.addDeaths(gameEndEvent.playersDeath.get(i));
            ps.refreshKd();
            session.merge(ps);
        }

        session.persist(match);
        for (int i = 0; i < dbPlayerList.size(); i++) {
            session.persist(stats.get(i));
        }

        session.getTransaction().commit();
    }


    private static DbPlayer getPlayerByUsername(String username) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<DbPlayer> criteria = builder.createQuery(DbPlayer.class);
        Root<DbPlayer> root = criteria.from(DbPlayer.class);
        criteria.select(root).where(builder.equal(root.get("username"), username));
        Query<DbPlayer> query = session.createQuery(criteria);
        DbPlayer player = query.uniqueResult();
        session.getTransaction().commit();
        session.close();
        return player;
    }

    public static boolean usernameAndPasswordIsValid(String username, String password) {
        DbPlayer dbPlayer = getPlayerByUsername(username);
        if (dbPlayer != null && dbPlayer.getPassword().equals(password)) {
            return true;
        }
        return false;
    }
}
