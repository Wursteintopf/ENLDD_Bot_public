public class User {

    private long id;
    private String user_name;
    private boolean admin;
    private boolean mod;
    private boolean operator;
    private boolean hightrust;
    private boolean guest;

    public User(long id, String user_name) {
        this.id = id;
        this.user_name = user_name;
        this.admin = false;
        this.mod = false;
        this.operator = false;
        this.hightrust = false;
        this.guest = false;

    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUser_name(){
        return this.user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public boolean getAdmin() {
        return this.admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public void changeAdmin() {
        if (this.admin == true) {
            this.admin = false;
        } else {
            this.admin = true;
        }
    }

    public boolean getMod() {
        return this.mod;
    }

    public void setMod(boolean mod) {
        this.mod = mod;
    }

    public void changeMod() {
        if (this.mod == true) {
            this.mod = false;
        } else {
            this.mod = true;
        }
    }

    public boolean getOperator() {
        return this.operator;
    }

    public void setOperator(boolean operator) {
        this.operator = operator;
    }

    public void changeOperator() {
        if (this.operator == true) {
            this.operator = false;
        } else {
            this.operator = true;
        }
    }

    public boolean getHightrust() {
        return this.hightrust;
    }

    public void setHightrust(boolean hightrust) {
        this.hightrust = hightrust;
    }

    public void changeHightrust() {
        if (this.hightrust == true) {
            this.hightrust = false;
        } else {
            this.hightrust = true;
        }
    }

    public boolean getGuest() {
        return this.guest;
    }

    public void setGuest(boolean guest) {
        this.guest = guest;
    }

    public void changeGuest() {
        if (this.guest == true) {
            this.guest = false;
        } else {
            this.guest = true;
        }
    }
}
