package kwee.garminSummary.lib;

public class Trip {
  private String m_KMstandeind;
  private String m_Day;
  private String m_Date;
  private String m_Origin;
  private String m_Finish;
  private String m_LongitudeOrigin;
  private String m_LatitudeOrigin;
  private String m_LongitudeFinish;
  private String m_LatitudeFinish;
  private String m_AddressOrigin;
  private String m_AddressFinish;
  private String m_Distance;
  private String m_Duration;
  private String m_AvrSpeed;

  /**
   * @return the m_KMstandeind
   */
  public String getKMstandEind() {
    return m_KMstandeind;
  }

  /**
   * @return the m_Day
   */
  public String getDay() {
    return m_Day;
  }

  /**
   * @return the m_Date
   */
  public String getDate() {
    return m_Date;
  }

  /**
   * @return the m_Origin
   */
  public String getOrigin() {
    return m_Origin;
  }

  /**
   * @return the m_Finish
   */
  public String getFinish() {
    return m_Finish;
  }

  /**
   * @return the m_LongitudeOrigin
   */
  public String getLongitudeOrigin() {
    return m_LongitudeOrigin;
  }

  /**
   * @return the m_LatitudeOrigin
   */
  public String getLatitudeOrigin() {
    return m_LatitudeOrigin;
  }

  /**
   * @return the m_LongitudeFinish
   */
  public String getLongitudeFinish() {
    return m_LongitudeFinish;
  }

  /**
   * @return the m_LatitudeFinish
   */
  public String getLatitudeFinish() {
    return m_LatitudeFinish;
  }

  /**
   * @return the m_AddressOrigin
   */
  public String getAddressOrigin() {
    return m_AddressOrigin;
  }

  /**
   * @return the m_AddressFinish
   */
  public String getAddressFinish() {
    return m_AddressFinish;
  }

  /**
   * @return the m_Distance
   */
  public String getDistance() {
    return m_Distance;
  }

  /**
   * @return the m_Duration
   */
  public String getDuration() {
    return m_Duration;
  }

  /**
   * @return the m_AvrSpeed
   */
  public String getAvrSpeed() {
    return m_AvrSpeed;
  }

  /**
   * @param m_KMstandeind the m_KMstandeind to set
   */
  public void setKMstandEind(String m_KMstandeind) {
    this.m_KMstandeind = m_KMstandeind;
  }

  /**
   * @param m_Day the m_Day to set
   */
  public void setDay(String m_Day) {
    this.m_Day = m_Day;
  }

  /**
   * @param m_Date the m_Date to set
   */
  public void setDate(String m_Date) {
    this.m_Date = m_Date;
  }

  /**
   * @param m_Origin the m_Origin to set
   */
  public void setOrigin(String m_Origin) {
    this.m_Origin = m_Origin;
  }

  /**
   * @param m_Finish the m_Finish to set
   */
  public void setFinish(String m_Finish) {
    this.m_Finish = m_Finish;
  }

  /**
   * @param m_LongitudeOrigin the m_LongitudeOrigin to set
   */
  public void setLongitudeOrigin(String m_LongitudeOrigin) {
    this.m_LongitudeOrigin = m_LongitudeOrigin;
  }

  /**
   * @param m_LatitudeOrigin the m_LatitudeOrigin to set
   */
  public void setLatitudeOrigin(String m_LatitudeOrigin) {
    this.m_LatitudeOrigin = m_LatitudeOrigin;
  }

  /**
   * @param m_LongitudeFinish the m_LongitudeFinish to set
   */
  public void setLongitudeFinish(String m_LongitudeFinish) {
    this.m_LongitudeFinish = m_LongitudeFinish;
  }

  /**
   * @param m_LatitudeFinish the m_LatitudeFinish to set
   */
  public void setLatitudeFinish(String m_LatitudeFinish) {
    this.m_LatitudeFinish = m_LatitudeFinish;
  }

  /**
   * @param m_AddressOrigin the m_AddressOrigin to set
   */
  public void setAddressOrigin(String m_AddressOrigin) {
    this.m_AddressOrigin = m_AddressOrigin;
  }

  /**
   * @param m_AddressFinish the m_AddressFinish to set
   */
  public void setAddressFinish(String m_AddressFinish) {
    this.m_AddressFinish = m_AddressFinish;
  }

  /**
   * @param m_Distance the m_Distance to set
   */
  public void setDistance(String m_Distance) {
    this.m_Distance = m_Distance;
  }

  /**
   * @param m_Duration the m_Duration to set
   */
  public void setDuration(String m_Duration) {
    this.m_Duration = m_Duration;
  }

  /**
   * @param m_AvrSpeed the m_AvrSpeed to set
   */
  public void setAvrSpeed(String m_AvrSpeed) {
    this.m_AvrSpeed = m_AvrSpeed;
  }

  public boolean isEmpty() {
    boolean bstat = true;
    bstat = m_KMstandeind.isBlank();
    bstat = bstat && m_Day.isBlank();
    bstat = bstat && m_Date.isBlank();
    bstat = bstat && m_Origin.isBlank();
    bstat = bstat && m_Finish.isBlank();
    bstat = bstat && m_LongitudeOrigin.isBlank();
    bstat = bstat && m_LatitudeOrigin.isBlank();
    bstat = bstat && m_LongitudeFinish.isBlank();
    bstat = bstat && m_LatitudeFinish.isBlank();
    bstat = bstat && m_AddressOrigin.isBlank();
    bstat = bstat && m_AddressFinish.isBlank();
    bstat = bstat && m_Distance.isBlank();
    bstat = bstat && m_Duration.isBlank();
    bstat = bstat && m_AvrSpeed.isBlank();
    return bstat;
  }

}
