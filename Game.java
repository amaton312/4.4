public class Game {
    // Keep fields as Strings so rows with non-numeric / missing values still load cleanly
    public String Name;
    public String Platform;
    public String Year_of_Release;
    public String Genre;
    public String Publisher;
    public String NA_Sales;
    public String EU_Sales;
    public String JP_Sales;
    public String Other_Sales;
    public String Global_Sales;
    public String Critic_Score;
    public String Critic_Count;
    public String User_Score;
    public String User_Count;
    public String Developer;
    public String Rating;

    @Override
    public String toString() {
        return "Title: " + Name + "\nPlatform: " + Platform + "\nGlobal Sales: " + Global_Sales + " million";
    }

    public Game() {
    }

    public Game(String Name, String Platform, String Year_of_Release, String Genre,
                String Publisher, String NA_Sales, String EU_Sales, String JP_Sales,
                String Other_Sales, String Global_Sales, String Critic_Score,
                String Critic_Count, String User_Score, String User_Count,
                String Developer, String Rating) {

        this.Name = Name;
        this.Platform = Platform;
        this.Year_of_Release = Year_of_Release;
        this.Genre = Genre;
        this.Publisher = Publisher;
        this.NA_Sales = NA_Sales;
        this.EU_Sales = EU_Sales;
        this.JP_Sales = JP_Sales;
        this.Other_Sales = Other_Sales;
        this.Global_Sales = Global_Sales;
        this.Critic_Score = Critic_Score;
        this.Critic_Count = Critic_Count;
        this.User_Score = User_Score;
        this.User_Count = User_Count;
        this.Developer = Developer;
        this.Rating = Rating;
    }
}