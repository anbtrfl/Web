package ru.itmo.wp.web.page;


import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class TicTacToePage {
    public static class State {
        private final int size;
        private String phase;
        private boolean crossesMove;
        private int empty;
        private final String[][] cells;

        public State() {
            size = 3;
            phase = "RUNNING";
            crossesMove = true;
            empty = size * size;
            cells = new String[size][size];
        }

        public int getSize() {
            return size;
        }

        public String getPhase() {
            return phase;
        }

        public boolean getCrossesMove() {
            return crossesMove;
        }

        public String[][] getCells() {
            return cells;
        }

        private String getCurrentMoveString() {
            return crossesMove ? "X" : "O";
        }

        private boolean check(int i0, int j0, int di, int dj) {
            if (cells[i0][j0] == null) {
                return false;
            }
            for (int t = 1; t < 3; ++t) {
                int i1 = i0 + t * di;
                int j1 = j0 + t * dj;
                if (!cells[i0][j0].equals(cells[i1][j1])) {
                    return false;
                }
            }
            return true;
        }

        private boolean checkWin() {
            boolean win = false;
            for (int i = 0; i < 3; ++i) {
                win |= check(i, 0, 0, 1);
                win |= check(0, i, 1, 0);
            }
            win |= check(0, 0, 1, 1);
            win |= check(2, 0, -1, 1);
            return win;
        }

        public void makeMove(int x, int y) {
            if (cells[x][y] != null || !phase.equals("RUNNING")) {
                return;
            }
            cells[x][y] = getCurrentMoveString();
            empty--;
            boolean win = checkWin();
            if (win) {
                phase = "WON" + "_" + getCurrentMoveString();
            } else if (empty == 0) {
                phase = "DRAW";
            }
            crossesMove = !crossesMove;
        }

    }


    private void action(Map<String, Object> view, HttpServletRequest request) {
        view.put("state", getState(request));
    }

    private State getState(HttpServletRequest request) {
        if (request.getSession().getAttribute("state") == null) {
            request.getSession().setAttribute("state", new State());
        }
        return (State) request.getSession().getAttribute("state");
    }

    private void onMove(Map<String, Object> view, HttpServletRequest request) {
        State state = getState(request);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (request.getParameter("cell_" + i + j) != null) {
                    state.makeMove(i, j);
                    break ;
                }
            }
        }
        request.setAttribute("state", state);
        action(view, request);
    }

    private void newGame(Map<String, Object> view, HttpServletRequest request) {
        request.getSession().setAttribute("state", new State());
        action(view, request);
    }
}