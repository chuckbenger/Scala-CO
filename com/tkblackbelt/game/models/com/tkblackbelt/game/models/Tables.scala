package com.tkblackbelt.game.models
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = scala.slick.driver.MySQLDriver
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: scala.slick.driver.JdbcProfile
  import profile.simple._
  import scala.slick.model.ForeignKeyAction
  import scala.slick.collection.heterogenous._
  import scala.slick.collection.heterogenous.syntax._
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import scala.slick.jdbc.{GetResult => GR}
  
  /** DDL for all tables. Call .create to execute. */
  lazy val ddl = Accounts.ddl ++ Characters.ddl ++ Enemys.ddl ++ Friends.ddl ++ Guilds.ddl ++ Items.ddl ++ Maps.ddl ++ Mobspawns.ddl ++ Monsters.ddl ++ Poleholder.ddl ++ Prof.ddl ++ Revpoints.ddl ++ Servers.ddl ++ Serverskill.ddl ++ Shops.ddl ++ Skills.ddl ++ Stats.ddl ++ Tnpcs.ddl ++ Tqnpcs.ddl
  
  /** Entity class storing rows of table Accounts
   *  @param accountid Database column AccountID 
   *  @param password Database column Password 
   *  @param `type` Database column Type Default(0)
   *  @param auth Database column Auth Default(1)
   *  @param address Database column Address  */
  case class AccountsRow(accountid: Option[String], password: Option[String], `type`: Int = 0, auth: Int = 1, address: Option[Int])
  /** GetResult implicit for fetching AccountsRow objects using plain SQL queries */
  implicit def GetResultAccountsRow(implicit e0: GR[Option[String]], e1: GR[Int], e2: GR[Option[Int]]): GR[AccountsRow] = GR{
    prs => import prs._
    AccountsRow.tupled((<<?[String], <<?[String], <<[Int], <<[Int], <<?[Int]))
  }
  /** Table description of table accounts. Objects of this class serve as prototypes for rows in queries.
   *  NOTE: The following names collided with Scala keywords and were escaped: type */
  class Accounts(tag: Tag) extends Table[AccountsRow](tag, "accounts") {
    def * = (accountid, password, `type`, auth, address) <> (AccountsRow.tupled, AccountsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (accountid, password, `type`.?, auth.?, address).shaped.<>({r=>import r._; _3.map(_=> AccountsRow.tupled((_1, _2, _3.get, _4.get, _5)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column AccountID  */
    val accountid: Column[Option[String]] = column[Option[String]]("AccountID")
    /** Database column Password  */
    val password: Column[Option[String]] = column[Option[String]]("Password")
    /** Database column Type Default(0)
     *  NOTE: The name was escaped because it collided with a Scala keyword. */
    val `type`: Column[Int] = column[Int]("Type", O.Default(0))
    /** Database column Auth Default(1) */
    val auth: Column[Int] = column[Int]("Auth", O.Default(1))
    /** Database column Address  */
    val address: Column[Option[Int]] = column[Option[Int]]("Address")
    
    /** Uniqueness Index over (accountid) (database name AccountID) */
    val index1 = index("AccountID", accountid, unique=true)
  }
  /** Collection-like TableQuery object for table Accounts */
  lazy val Accounts = new TableQuery(tag => new Accounts(tag))
  
  /** Row type of table Characters */
  type CharactersRow = HCons[Int,HCons[String,HCons[String,HCons[String,HCons[String,HCons[Int,HCons[Long,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[String,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Option[Int],HCons[Option[Int],HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Option[java.sql.Timestamp],HCons[Option[Int],HCons[Option[Int],HCons[Option[Int],HCons[Option[Int],HCons[Option[Int],HNil]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]
  /** Constructor for CharactersRow providing default values if available in the database schema. */
  def CharactersRow(charid: Int, name: String, account: String, server: String, spouse: String, level: Int = 1, exp: Long = 0L, str: Int = 0, dex: Int = 0, spi: Int = 0, vit: Int = 0, hp: Int = 1, mp: Int = 0, pkpoints: Int = 0, statpoints: Int = 0, money: Int = 0, cpoints: Int = 0, vpoints: Int = 0, whmoney: Int = 0, hairstyle: Int = 0, model: Int = 0, map: Int = 1002, mapinstance: Int = 0, xcord: Int = 438, ycord: Int = 377, status: String, gdonation: Int = 0, reborn: Int = 0, isgm: Int = 0, nobility: Int = 0, guild: Option[Int] = Some(0), grank: Option[Int] = Some(50), `class`: Int = 0, honor: Int = 0, ispm: Int = 0, firstlog: Int = 0, dbexpused: Option[java.sql.Timestamp] = None, exppotiontime: Option[Int] = Some(0), exppotionrate: Option[Int] = Some(0), previousmap: Option[Int] = Some(1002), houseid: Option[Int] = Some(0), housetype: Option[Int] = Some(0)): CharactersRow = {
    charid :: name :: account :: server :: spouse :: level :: exp :: str :: dex :: spi :: vit :: hp :: mp :: pkpoints :: statpoints :: money :: cpoints :: vpoints :: whmoney :: hairstyle :: model :: map :: mapinstance :: xcord :: ycord :: status :: gdonation :: reborn :: isgm :: nobility :: guild :: grank :: `class` :: honor :: ispm :: firstlog :: dbexpused :: exppotiontime :: exppotionrate :: previousmap :: houseid :: housetype :: HNil
  }
  /** GetResult implicit for fetching CharactersRow objects using plain SQL queries */
  implicit def GetResultCharactersRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Long], e3: GR[Option[Int]], e4: GR[Option[java.sql.Timestamp]]): GR[CharactersRow] = GR{
    prs => import prs._
    <<[Int] :: <<[String] :: <<[String] :: <<[String] :: <<[String] :: <<[Int] :: <<[Long] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[String] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<?[Int] :: <<?[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<?[java.sql.Timestamp] :: <<?[Int] :: <<?[Int] :: <<?[Int] :: <<?[Int] :: <<?[Int] :: HNil
  }
  /** Table description of table characters. Objects of this class serve as prototypes for rows in queries.
   *  NOTE: The following names collided with Scala keywords and were escaped: class */
  class Characters(tag: Tag) extends Table[CharactersRow](tag, "characters") {
    def * = charid :: name :: account :: server :: spouse :: level :: exp :: str :: dex :: spi :: vit :: hp :: mp :: pkpoints :: statpoints :: money :: cpoints :: vpoints :: whmoney :: hairstyle :: model :: map :: mapinstance :: xcord :: ycord :: status :: gdonation :: reborn :: isgm :: nobility :: guild :: grank :: `class` :: honor :: ispm :: firstlog :: dbexpused :: exppotiontime :: exppotionrate :: previousmap :: houseid :: housetype :: HNil
    
    /** Database column CharID AutoInc, PrimaryKey */
    val charid: Column[Int] = column[Int]("CharID", O.AutoInc, O.PrimaryKey)
    /** Database column Name  */
    val name: Column[String] = column[String]("Name")
    /** Database column Account  */
    val account: Column[String] = column[String]("Account")
    /** Database column Server  */
    val server: Column[String] = column[String]("Server")
    /** Database column Spouse  */
    val spouse: Column[String] = column[String]("Spouse")
    /** Database column Level Default(1) */
    val level: Column[Int] = column[Int]("Level", O.Default(1))
    /** Database column Exp Default(0) */
    val exp: Column[Long] = column[Long]("Exp", O.Default(0L))
    /** Database column Str Default(0) */
    val str: Column[Int] = column[Int]("Str", O.Default(0))
    /** Database column Dex Default(0) */
    val dex: Column[Int] = column[Int]("Dex", O.Default(0))
    /** Database column Spi Default(0) */
    val spi: Column[Int] = column[Int]("Spi", O.Default(0))
    /** Database column Vit Default(0) */
    val vit: Column[Int] = column[Int]("Vit", O.Default(0))
    /** Database column HP Default(1) */
    val hp: Column[Int] = column[Int]("HP", O.Default(1))
    /** Database column MP Default(0) */
    val mp: Column[Int] = column[Int]("MP", O.Default(0))
    /** Database column PkPoints Default(0) */
    val pkpoints: Column[Int] = column[Int]("PkPoints", O.Default(0))
    /** Database column StatPoints Default(0) */
    val statpoints: Column[Int] = column[Int]("StatPoints", O.Default(0))
    /** Database column Money Default(0) */
    val money: Column[Int] = column[Int]("Money", O.Default(0))
    /** Database column CPoints Default(0) */
    val cpoints: Column[Int] = column[Int]("CPoints", O.Default(0))
    /** Database column VPoints Default(0) */
    val vpoints: Column[Int] = column[Int]("VPoints", O.Default(0))
    /** Database column WHMoney Default(0) */
    val whmoney: Column[Int] = column[Int]("WHMoney", O.Default(0))
    /** Database column HairStyle Default(0) */
    val hairstyle: Column[Int] = column[Int]("HairStyle", O.Default(0))
    /** Database column Model Default(0) */
    val model: Column[Int] = column[Int]("Model", O.Default(0))
    /** Database column Map Default(1002) */
    val map: Column[Int] = column[Int]("Map", O.Default(1002))
    /** Database column MapInstance Default(0) */
    val mapinstance: Column[Int] = column[Int]("MapInstance", O.Default(0))
    /** Database column xCord Default(438) */
    val xcord: Column[Int] = column[Int]("xCord", O.Default(438))
    /** Database column yCord Default(377) */
    val ycord: Column[Int] = column[Int]("yCord", O.Default(377))
    /** Database column Status  */
    val status: Column[String] = column[String]("Status")
    /** Database column GDonation Default(0) */
    val gdonation: Column[Int] = column[Int]("GDonation", O.Default(0))
    /** Database column Reborn Default(0) */
    val reborn: Column[Int] = column[Int]("Reborn", O.Default(0))
    /** Database column isGM Default(0) */
    val isgm: Column[Int] = column[Int]("isGM", O.Default(0))
    /** Database column nobility Default(0) */
    val nobility: Column[Int] = column[Int]("nobility", O.Default(0))
    /** Database column Guild Default(Some(0)) */
    val guild: Column[Option[Int]] = column[Option[Int]]("Guild", O.Default(Some(0)))
    /** Database column GRank Default(Some(50)) */
    val grank: Column[Option[Int]] = column[Option[Int]]("GRank", O.Default(Some(50)))
    /** Database column Class Default(0)
     *  NOTE: The name was escaped because it collided with a Scala keyword. */
    val `class`: Column[Int] = column[Int]("Class", O.Default(0))
    /** Database column Honor Default(0) */
    val honor: Column[Int] = column[Int]("Honor", O.Default(0))
    /** Database column isPM Default(0) */
    val ispm: Column[Int] = column[Int]("isPM", O.Default(0))
    /** Database column FirstLog Default(0) */
    val firstlog: Column[Int] = column[Int]("FirstLog", O.Default(0))
    /** Database column DbExpUsed Default(None) */
    val dbexpused: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("DbExpUsed", O.Default(None))
    /** Database column ExpPotionTime Default(Some(0)) */
    val exppotiontime: Column[Option[Int]] = column[Option[Int]]("ExpPotionTime", O.Default(Some(0)))
    /** Database column ExpPotionRate Default(Some(0)) */
    val exppotionrate: Column[Option[Int]] = column[Option[Int]]("ExpPotionRate", O.Default(Some(0)))
    /** Database column PreviousMap Default(Some(1002)) */
    val previousmap: Column[Option[Int]] = column[Option[Int]]("PreviousMap", O.Default(Some(1002)))
    /** Database column HouseID Default(Some(0)) */
    val houseid: Column[Option[Int]] = column[Option[Int]]("HouseID", O.Default(Some(0)))
    /** Database column HouseType Default(Some(0)) */
    val housetype: Column[Option[Int]] = column[Option[Int]]("HouseType", O.Default(Some(0)))
  }
  /** Collection-like TableQuery object for table Characters */
  lazy val Characters = new TableQuery(tag => new Characters(tag))
  
  /** Entity class storing rows of table Enemys
   *  @param charid Database column CharID 
   *  @param enemyid Database column EnemyID 
   *  @param enemyname Database column EnemyName  */
  case class EnemysRow(charid: Int, enemyid: Int, enemyname: String)
  /** GetResult implicit for fetching EnemysRow objects using plain SQL queries */
  implicit def GetResultEnemysRow(implicit e0: GR[Int], e1: GR[String]): GR[EnemysRow] = GR{
    prs => import prs._
    EnemysRow.tupled((<<[Int], <<[Int], <<[String]))
  }
  /** Table description of table enemys. Objects of this class serve as prototypes for rows in queries. */
  class Enemys(tag: Tag) extends Table[EnemysRow](tag, "enemys") {
    def * = (charid, enemyid, enemyname) <> (EnemysRow.tupled, EnemysRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (charid.?, enemyid.?, enemyname.?).shaped.<>({r=>import r._; _1.map(_=> EnemysRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column CharID  */
    val charid: Column[Int] = column[Int]("CharID")
    /** Database column EnemyID  */
    val enemyid: Column[Int] = column[Int]("EnemyID")
    /** Database column EnemyName  */
    val enemyname: Column[String] = column[String]("EnemyName")
  }
  /** Collection-like TableQuery object for table Enemys */
  lazy val Enemys = new TableQuery(tag => new Enemys(tag))
  
  /** Entity class storing rows of table Friends
   *  @param charid Database column CharID 
   *  @param friendid Database column FriendID 
   *  @param friendname Database column FriendName  */
  case class FriendsRow(charid: Option[Int], friendid: Option[Int], friendname: Option[String])
  /** GetResult implicit for fetching FriendsRow objects using plain SQL queries */
  implicit def GetResultFriendsRow(implicit e0: GR[Option[Int]], e1: GR[Option[String]]): GR[FriendsRow] = GR{
    prs => import prs._
    FriendsRow.tupled((<<?[Int], <<?[Int], <<?[String]))
  }
  /** Table description of table friends. Objects of this class serve as prototypes for rows in queries. */
  class Friends(tag: Tag) extends Table[FriendsRow](tag, "friends") {
    def * = (charid, friendid, friendname) <> (FriendsRow.tupled, FriendsRow.unapply)
    
    /** Database column CharID  */
    val charid: Column[Option[Int]] = column[Option[Int]]("CharID")
    /** Database column FriendID  */
    val friendid: Column[Option[Int]] = column[Option[Int]]("FriendID")
    /** Database column FriendName  */
    val friendname: Column[Option[String]] = column[Option[String]]("FriendName")
  }
  /** Collection-like TableQuery object for table Friends */
  lazy val Friends = new TableQuery(tag => new Friends(tag))
  
  /** Entity class storing rows of table Guilds
   *  @param guildid Database column GuildID AutoInc, PrimaryKey
   *  @param name Database column Name 
   *  @param leader Database column Leader 
   *  @param fund Database column Fund Default(0)
   *  @param bulletin Database column Bulletin 
   *  @param enemies Database column Enemies 
   *  @param allies Database column Allies  */
  case class GuildsRow(guildid: Int, name: String, leader: String, fund: Int = 0, bulletin: String, enemies: String, allies: String)
  /** GetResult implicit for fetching GuildsRow objects using plain SQL queries */
  implicit def GetResultGuildsRow(implicit e0: GR[Int], e1: GR[String]): GR[GuildsRow] = GR{
    prs => import prs._
    GuildsRow.tupled((<<[Int], <<[String], <<[String], <<[Int], <<[String], <<[String], <<[String]))
  }
  /** Table description of table guilds. Objects of this class serve as prototypes for rows in queries. */
  class Guilds(tag: Tag) extends Table[GuildsRow](tag, "guilds") {
    def * = (guildid, name, leader, fund, bulletin, enemies, allies) <> (GuildsRow.tupled, GuildsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (guildid.?, name.?, leader.?, fund.?, bulletin.?, enemies.?, allies.?).shaped.<>({r=>import r._; _1.map(_=> GuildsRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column GuildID AutoInc, PrimaryKey */
    val guildid: Column[Int] = column[Int]("GuildID", O.AutoInc, O.PrimaryKey)
    /** Database column Name  */
    val name: Column[String] = column[String]("Name")
    /** Database column Leader  */
    val leader: Column[String] = column[String]("Leader")
    /** Database column Fund Default(0) */
    val fund: Column[Int] = column[Int]("Fund", O.Default(0))
    /** Database column Bulletin  */
    val bulletin: Column[String] = column[String]("Bulletin")
    /** Database column Enemies  */
    val enemies: Column[String] = column[String]("Enemies")
    /** Database column Allies  */
    val allies: Column[String] = column[String]("Allies")
  }
  /** Collection-like TableQuery object for table Guilds */
  lazy val Guilds = new TableQuery(tag => new Guilds(tag))
  
  /** Entity class storing rows of table Items
   *  @param charid Database column CharID Default(0)
   *  @param itemuid Database column ItemUID AutoInc, PrimaryKey
   *  @param position Database column Position Default(0)
   *  @param itemid Database column ItemID Default(0)
   *  @param plus Database column Plus Default(12)
   *  @param soc1 Database column Soc1 Default(13)
   *  @param soc2 Database column Soc2 Default(13)
   *  @param dura Database column Dura Default(2530)
   *  @param maxdura Database column MaxDura Default(2560)
   *  @param color Database column Color Default(Some(3)) */
  case class ItemsRow(charid: Int = 0, itemuid: Int, position: Int = 0, itemid: Int = 0, plus: Int = 12, soc1: Int = 13, soc2: Int = 13, dura: Int = 2530, maxdura: Int = 2560, color: Option[Int] = Some(3))
  /** GetResult implicit for fetching ItemsRow objects using plain SQL queries */
  implicit def GetResultItemsRow(implicit e0: GR[Int], e1: GR[Option[Int]]): GR[ItemsRow] = GR{
    prs => import prs._
    ItemsRow.tupled((<<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<?[Int]))
  }
  /** Table description of table items. Objects of this class serve as prototypes for rows in queries. */
  class Items(tag: Tag) extends Table[ItemsRow](tag, "items") {
    def * = (charid, itemuid, position, itemid, plus, soc1, soc2, dura, maxdura, color) <> (ItemsRow.tupled, ItemsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (charid.?, itemuid.?, position.?, itemid.?, plus.?, soc1.?, soc2.?, dura.?, maxdura.?, color).shaped.<>({r=>import r._; _1.map(_=> ItemsRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column CharID Default(0) */
    val charid: Column[Int] = column[Int]("CharID", O.Default(0))
    /** Database column ItemUID AutoInc, PrimaryKey */
    val itemuid: Column[Int] = column[Int]("ItemUID", O.AutoInc, O.PrimaryKey)
    /** Database column Position Default(0) */
    val position: Column[Int] = column[Int]("Position", O.Default(0))
    /** Database column ItemID Default(0) */
    val itemid: Column[Int] = column[Int]("ItemID", O.Default(0))
    /** Database column Plus Default(12) */
    val plus: Column[Int] = column[Int]("Plus", O.Default(12))
    /** Database column Soc1 Default(13) */
    val soc1: Column[Int] = column[Int]("Soc1", O.Default(13))
    /** Database column Soc2 Default(13) */
    val soc2: Column[Int] = column[Int]("Soc2", O.Default(13))
    /** Database column Dura Default(2530) */
    val dura: Column[Int] = column[Int]("Dura", O.Default(2530))
    /** Database column MaxDura Default(2560) */
    val maxdura: Column[Int] = column[Int]("MaxDura", O.Default(2560))
    /** Database column Color Default(Some(3)) */
    val color: Column[Option[Int]] = column[Option[Int]]("Color", O.Default(Some(3)))
  }
  /** Collection-like TableQuery object for table Items */
  lazy val Items = new TableQuery(tag => new Items(tag))
  
  /** Entity class storing rows of table Maps
   *  @param id Database column id Default(0), PrimaryKey
   *  @param mapdoc Database column mapdoc Default(0)
   *  @param `type` Database column type Default(Some(0)) */
  case class MapsRow(id: Int = 0, mapdoc: Int = 0, `type`: Option[Int] = Some(0))
  /** GetResult implicit for fetching MapsRow objects using plain SQL queries */
  implicit def GetResultMapsRow(implicit e0: GR[Int], e1: GR[Option[Int]]): GR[MapsRow] = GR{
    prs => import prs._
    MapsRow.tupled((<<[Int], <<[Int], <<?[Int]))
  }
  /** Table description of table maps. Objects of this class serve as prototypes for rows in queries.
   *  NOTE: The following names collided with Scala keywords and were escaped: type */
  class Maps(tag: Tag) extends Table[MapsRow](tag, "maps") {
    def * = (id, mapdoc, `type`) <> (MapsRow.tupled, MapsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, mapdoc.?, `type`).shaped.<>({r=>import r._; _1.map(_=> MapsRow.tupled((_1.get, _2.get, _3)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id Default(0), PrimaryKey */
    val id: Column[Int] = column[Int]("id", O.Default(0), O.PrimaryKey)
    /** Database column mapdoc Default(0) */
    val mapdoc: Column[Int] = column[Int]("mapdoc", O.Default(0))
    /** Database column type Default(Some(0))
     *  NOTE: The name was escaped because it collided with a Scala keyword. */
    val `type`: Column[Option[Int]] = column[Option[Int]]("type", O.Default(Some(0)))
  }
  /** Collection-like TableQuery object for table Maps */
  lazy val Maps = new TableQuery(tag => new Maps(tag))
  
  /** Entity class storing rows of table Mobspawns
   *  @param unispawnid Database column UniSpawnID AutoInc, PrimaryKey
   *  @param map Database column Map Default(0)
   *  @param x-start Database column x-start Default(0)
   *  @param y-start Database column y-start Default(0)
   *  @param x-stop Database column x-stop Default(0)
   *  @param y-stop Database column y-stop Default(0)
   *  @param numbertospawn Database column NumberToSpawn Default(0)
   *  @param restSecs Database column rest_secs Default(0)
   *  @param numbertospawnf Database column NumberToSpawnf Default(0)
   *  @param id Database column ID Default(0)
   *  @param timerBegin Database column timer_begin Default(0)
   *  @param timerEnd Database column timer_end Default(0)
   *  @param bornX Database column born_x Default(0)
   *  @param bornY Database column born_y Default(0) */
  case class MobspawnsRow(unispawnid: Int, map: Int = 0, x-start: Int = 0, y-start: Int = 0, x-stop: Int = 0, y-stop: Int = 0, numbertospawn: Int = 0, restSecs: Int = 0, numbertospawnf: Int = 0, id: Int = 0, timerBegin: Int = 0, timerEnd: Int = 0, bornX: Int = 0, bornY: Int = 0)
  /** GetResult implicit for fetching MobspawnsRow objects using plain SQL queries */
  implicit def GetResultMobspawnsRow(implicit e0: GR[Int]): GR[MobspawnsRow] = GR{
    prs => import prs._
    MobspawnsRow.tupled((<<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int]))
  }
  /** Table description of table mobspawns. Objects of this class serve as prototypes for rows in queries. */
  class Mobspawns(tag: Tag) extends Table[MobspawnsRow](tag, "mobspawns") {
    def * = (unispawnid, map, x-start, y-start, x-stop, y-stop, numbertospawn, restSecs, numbertospawnf, id, timerBegin, timerEnd, bornX, bornY) <> (MobspawnsRow.tupled, MobspawnsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (unispawnid.?, map.?, x-start.?, y-start.?, x-stop.?, y-stop.?, numbertospawn.?, restSecs.?, numbertospawnf.?, id.?, timerBegin.?, timerEnd.?, bornX.?, bornY.?).shaped.<>({r=>import r._; _1.map(_=> MobspawnsRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get, _12.get, _13.get, _14.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column UniSpawnID AutoInc, PrimaryKey */
    val unispawnid: Column[Int] = column[Int]("UniSpawnID", O.AutoInc, O.PrimaryKey)
    /** Database column Map Default(0) */
    val map: Column[Int] = column[Int]("Map", O.Default(0))
    /** Database column x-start Default(0) */
    val x-start: Column[Int] = column[Int]("x-start", O.Default(0))
    /** Database column y-start Default(0) */
    val y-start: Column[Int] = column[Int]("y-start", O.Default(0))
    /** Database column x-stop Default(0) */
    val x-stop: Column[Int] = column[Int]("x-stop", O.Default(0))
    /** Database column y-stop Default(0) */
    val y-stop: Column[Int] = column[Int]("y-stop", O.Default(0))
    /** Database column NumberToSpawn Default(0) */
    val numbertospawn: Column[Int] = column[Int]("NumberToSpawn", O.Default(0))
    /** Database column rest_secs Default(0) */
    val restSecs: Column[Int] = column[Int]("rest_secs", O.Default(0))
    /** Database column NumberToSpawnf Default(0) */
    val numbertospawnf: Column[Int] = column[Int]("NumberToSpawnf", O.Default(0))
    /** Database column ID Default(0) */
    val id: Column[Int] = column[Int]("ID", O.Default(0))
    /** Database column timer_begin Default(0) */
    val timerBegin: Column[Int] = column[Int]("timer_begin", O.Default(0))
    /** Database column timer_end Default(0) */
    val timerEnd: Column[Int] = column[Int]("timer_end", O.Default(0))
    /** Database column born_x Default(0) */
    val bornX: Column[Int] = column[Int]("born_x", O.Default(0))
    /** Database column born_y Default(0) */
    val bornY: Column[Int] = column[Int]("born_y", O.Default(0))
    
    /** Index over (map) (database name mapid) */
    val index1 = index("mapid", map)
  }
  /** Collection-like TableQuery object for table Mobspawns */
  lazy val Mobspawns = new TableQuery(tag => new Mobspawns(tag))
  
  /** Row type of table Monsters */
  type MonstersRow = HCons[Int,HCons[String,HCons[Int,HCons[Int,HCons[Short,HCons[Short,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Short,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HNil]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]
  /** Constructor for MonstersRow providing default values if available in the database schema. */
  def MonstersRow(id: Int = 0, name: String, `type`: Int = 0, mech: Int = 0, hp: Short = 0, mana: Short = 0, atkmin: Int = 0, atkmax: Int = 0, pdef: Int = 0, dex: Int = 0, dodge: Int = 0, wander: Int = 0, hunter: Int = 0, guard: Int = 0, arange: Int = 0, jrange: Int = 0, escapeLife: Int = 0, attackSpeed: Int = 0, speed: Int = 0, level: Int = 0, agressive: Int = 3, dropMoney: Int = 0, dropItemtype: Int = 0, sizeadd: Int = 0, action: Int = 0, runSpeed: Int = 0, atype: Int = 0, mdef: Int = 0, stcType: Short = 0, canrun: Int = 0, maxmoney: Int = 0, avgmoney: Int = 0, dchance: Int = 0, viewdistance: Int = 0, dropminlevel: Int = 0, dropmaxlevel: Int = 0, lvlb: Int = 0): MonstersRow = {
    id :: name :: `type` :: mech :: hp :: mana :: atkmin :: atkmax :: pdef :: dex :: dodge :: wander :: hunter :: guard :: arange :: jrange :: escapeLife :: attackSpeed :: speed :: level :: agressive :: dropMoney :: dropItemtype :: sizeadd :: action :: runSpeed :: atype :: mdef :: stcType :: canrun :: maxmoney :: avgmoney :: dchance :: viewdistance :: dropminlevel :: dropmaxlevel :: lvlb :: HNil
  }
  /** GetResult implicit for fetching MonstersRow objects using plain SQL queries */
  implicit def GetResultMonstersRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Short]): GR[MonstersRow] = GR{
    prs => import prs._
    <<[Int] :: <<[String] :: <<[Int] :: <<[Int] :: <<[Short] :: <<[Short] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Short] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: HNil
  }
  /** Table description of table monsters. Objects of this class serve as prototypes for rows in queries.
   *  NOTE: The following names collided with Scala keywords and were escaped: type */
  class Monsters(tag: Tag) extends Table[MonstersRow](tag, "monsters") {
    def * = id :: name :: `type` :: mech :: hp :: mana :: atkmin :: atkmax :: pdef :: dex :: dodge :: wander :: hunter :: guard :: arange :: jrange :: escapeLife :: attackSpeed :: speed :: level :: agressive :: dropMoney :: dropItemtype :: sizeadd :: action :: runSpeed :: atype :: mdef :: stcType :: canrun :: maxmoney :: avgmoney :: dchance :: viewdistance :: dropminlevel :: dropmaxlevel :: lvlb :: HNil
    
    /** Database column id Default(0), PrimaryKey */
    val id: Column[Int] = column[Int]("id", O.Default(0), O.PrimaryKey)
    /** Database column name  */
    val name: Column[String] = column[String]("name")
    /** Database column type Default(0)
     *  NOTE: The name was escaped because it collided with a Scala keyword. */
    val `type`: Column[Int] = column[Int]("type", O.Default(0))
    /** Database column mech Default(0) */
    val mech: Column[Int] = column[Int]("mech", O.Default(0))
    /** Database column hp Default(0) */
    val hp: Column[Short] = column[Short]("hp", O.Default(0))
    /** Database column mana Default(0) */
    val mana: Column[Short] = column[Short]("mana", O.Default(0))
    /** Database column atkmin Default(0) */
    val atkmin: Column[Int] = column[Int]("atkmin", O.Default(0))
    /** Database column atkmax Default(0) */
    val atkmax: Column[Int] = column[Int]("atkmax", O.Default(0))
    /** Database column pdef Default(0) */
    val pdef: Column[Int] = column[Int]("pdef", O.Default(0))
    /** Database column dex Default(0) */
    val dex: Column[Int] = column[Int]("dex", O.Default(0))
    /** Database column dodge Default(0) */
    val dodge: Column[Int] = column[Int]("dodge", O.Default(0))
    /** Database column wander Default(0) */
    val wander: Column[Int] = column[Int]("wander", O.Default(0))
    /** Database column hunter Default(0) */
    val hunter: Column[Int] = column[Int]("hunter", O.Default(0))
    /** Database column guard Default(0) */
    val guard: Column[Int] = column[Int]("guard", O.Default(0))
    /** Database column arange Default(0) */
    val arange: Column[Int] = column[Int]("arange", O.Default(0))
    /** Database column jrange Default(0) */
    val jrange: Column[Int] = column[Int]("jrange", O.Default(0))
    /** Database column escape_life Default(0) */
    val escapeLife: Column[Int] = column[Int]("escape_life", O.Default(0))
    /** Database column attack_speed Default(0) */
    val attackSpeed: Column[Int] = column[Int]("attack_speed", O.Default(0))
    /** Database column speed Default(0) */
    val speed: Column[Int] = column[Int]("speed", O.Default(0))
    /** Database column level Default(0) */
    val level: Column[Int] = column[Int]("level", O.Default(0))
    /** Database column agressive Default(3) */
    val agressive: Column[Int] = column[Int]("agressive", O.Default(3))
    /** Database column drop_money Default(0) */
    val dropMoney: Column[Int] = column[Int]("drop_money", O.Default(0))
    /** Database column drop_itemtype Default(0) */
    val dropItemtype: Column[Int] = column[Int]("drop_itemtype", O.Default(0))
    /** Database column sizeadd Default(0) */
    val sizeadd: Column[Int] = column[Int]("sizeadd", O.Default(0))
    /** Database column action Default(0) */
    val action: Column[Int] = column[Int]("action", O.Default(0))
    /** Database column run_speed Default(0) */
    val runSpeed: Column[Int] = column[Int]("run_speed", O.Default(0))
    /** Database column atype Default(0) */
    val atype: Column[Int] = column[Int]("atype", O.Default(0))
    /** Database column mdef Default(0) */
    val mdef: Column[Int] = column[Int]("mdef", O.Default(0))
    /** Database column stc_type Default(0) */
    val stcType: Column[Short] = column[Short]("stc_type", O.Default(0))
    /** Database column canrun Default(0) */
    val canrun: Column[Int] = column[Int]("canrun", O.Default(0))
    /** Database column maxmoney Default(0) */
    val maxmoney: Column[Int] = column[Int]("maxmoney", O.Default(0))
    /** Database column avgmoney Default(0) */
    val avgmoney: Column[Int] = column[Int]("avgmoney", O.Default(0))
    /** Database column dchance Default(0) */
    val dchance: Column[Int] = column[Int]("dchance", O.Default(0))
    /** Database column viewdistance Default(0) */
    val viewdistance: Column[Int] = column[Int]("viewdistance", O.Default(0))
    /** Database column dropminlevel Default(0) */
    val dropminlevel: Column[Int] = column[Int]("dropminlevel", O.Default(0))
    /** Database column dropmaxlevel Default(0) */
    val dropmaxlevel: Column[Int] = column[Int]("dropmaxlevel", O.Default(0))
    /** Database column lvlb Default(0) */
    val lvlb: Column[Int] = column[Int]("lvlb", O.Default(0))
  }
  /** Collection-like TableQuery object for table Monsters */
  lazy val Monsters = new TableQuery(tag => new Monsters(tag))
  
  /** Entity class storing rows of table Poleholder
   *  @param polename Database column PoleName Default(None) */
  case class PoleholderRow(polename: Option[String] = None)
  /** GetResult implicit for fetching PoleholderRow objects using plain SQL queries */
  implicit def GetResultPoleholderRow(implicit e0: GR[Option[String]]): GR[PoleholderRow] = GR{
    prs => import prs._
    PoleholderRow(<<?[String])
  }
  /** Table description of table poleholder. Objects of this class serve as prototypes for rows in queries. */
  class Poleholder(tag: Tag) extends Table[PoleholderRow](tag, "poleholder") {
    def * = polename <> (PoleholderRow, PoleholderRow.unapply)
    
    /** Database column PoleName Default(None) */
    val polename: Column[Option[String]] = column[Option[String]]("PoleName", O.Default(None))
  }
  /** Collection-like TableQuery object for table Poleholder */
  lazy val Poleholder = new TableQuery(tag => new Poleholder(tag))
  
  /** Entity class storing rows of table Prof
   *  @param charid Database column CharID 
   *  @param profid Database column ProfID 
   *  @param proflvl Database column ProfLvl 
   *  @param profexp Database column ProfExp Default(Some(0)) */
  case class ProfRow(charid: Option[Int], profid: Option[Int], proflvl: Option[Int], profexp: Option[Int] = Some(0))
  /** GetResult implicit for fetching ProfRow objects using plain SQL queries */
  implicit def GetResultProfRow(implicit e0: GR[Option[Int]]): GR[ProfRow] = GR{
    prs => import prs._
    ProfRow.tupled((<<?[Int], <<?[Int], <<?[Int], <<?[Int]))
  }
  /** Table description of table prof. Objects of this class serve as prototypes for rows in queries. */
  class Prof(tag: Tag) extends Table[ProfRow](tag, "prof") {
    def * = (charid, profid, proflvl, profexp) <> (ProfRow.tupled, ProfRow.unapply)
    
    /** Database column CharID  */
    val charid: Column[Option[Int]] = column[Option[Int]]("CharID")
    /** Database column ProfID  */
    val profid: Column[Option[Int]] = column[Option[Int]]("ProfID")
    /** Database column ProfLvl  */
    val proflvl: Column[Option[Int]] = column[Option[Int]]("ProfLvl")
    /** Database column ProfExp Default(Some(0)) */
    val profexp: Column[Option[Int]] = column[Option[Int]]("ProfExp", O.Default(Some(0)))
  }
  /** Collection-like TableQuery object for table Prof */
  lazy val Prof = new TableQuery(tag => new Prof(tag))
  
  /** Entity class storing rows of table Revpoints
   *  @param mapid Database column MapID 
   *  @param revivemapid Database column ReviveMapID 
   *  @param revivex Database column ReviveX 
   *  @param revivey Database column ReviveY  */
  case class RevpointsRow(mapid: Int, revivemapid: Int, revivex: Int, revivey: Int)
  /** GetResult implicit for fetching RevpointsRow objects using plain SQL queries */
  implicit def GetResultRevpointsRow(implicit e0: GR[Int]): GR[RevpointsRow] = GR{
    prs => import prs._
    RevpointsRow.tupled((<<[Int], <<[Int], <<[Int], <<[Int]))
  }
  /** Table description of table revpoints. Objects of this class serve as prototypes for rows in queries. */
  class Revpoints(tag: Tag) extends Table[RevpointsRow](tag, "revpoints") {
    def * = (mapid, revivemapid, revivex, revivey) <> (RevpointsRow.tupled, RevpointsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (mapid.?, revivemapid.?, revivex.?, revivey.?).shaped.<>({r=>import r._; _1.map(_=> RevpointsRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column MapID  */
    val mapid: Column[Int] = column[Int]("MapID")
    /** Database column ReviveMapID  */
    val revivemapid: Column[Int] = column[Int]("ReviveMapID")
    /** Database column ReviveX  */
    val revivex: Column[Int] = column[Int]("ReviveX")
    /** Database column ReviveY  */
    val revivey: Column[Int] = column[Int]("ReviveY")
  }
  /** Collection-like TableQuery object for table Revpoints */
  lazy val Revpoints = new TableQuery(tag => new Revpoints(tag))
  
  /** Entity class storing rows of table Servers
   *  @param servername Database column Servername 
   *  @param serverip Database column ServerIP 
   *  @param serverport Database column ServerPort Default(0) */
  case class ServersRow(servername: String, serverip: String, serverport: Int = 0)
  /** GetResult implicit for fetching ServersRow objects using plain SQL queries */
  implicit def GetResultServersRow(implicit e0: GR[String], e1: GR[Int]): GR[ServersRow] = GR{
    prs => import prs._
    ServersRow.tupled((<<[String], <<[String], <<[Int]))
  }
  /** Table description of table servers. Objects of this class serve as prototypes for rows in queries. */
  class Servers(tag: Tag) extends Table[ServersRow](tag, "servers") {
    def * = (servername, serverip, serverport) <> (ServersRow.tupled, ServersRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (servername.?, serverip.?, serverport.?).shaped.<>({r=>import r._; _1.map(_=> ServersRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column Servername  */
    val servername: Column[String] = column[String]("Servername")
    /** Database column ServerIP  */
    val serverip: Column[String] = column[String]("ServerIP")
    /** Database column ServerPort Default(0) */
    val serverport: Column[Int] = column[Int]("ServerPort", O.Default(0))
    
    /** Uniqueness Index over (servername) (database name Servername) */
    val index1 = index("Servername", servername, unique=true)
  }
  /** Collection-like TableQuery object for table Servers */
  lazy val Servers = new TableQuery(tag => new Servers(tag))
  
  /** Entity class storing rows of table Serverskill
   *  @param `type` Database column type Default(0)
   *  @param level Database column level Default(0)
   *  @param needExp Database column need_exp Default(0)
   *  @param needLevel Database column need_level Default(0) */
  case class ServerskillRow(`type`: Int = 0, level: Int = 0, needExp: Int = 0, needLevel: Int = 0)
  /** GetResult implicit for fetching ServerskillRow objects using plain SQL queries */
  implicit def GetResultServerskillRow(implicit e0: GR[Int]): GR[ServerskillRow] = GR{
    prs => import prs._
    ServerskillRow.tupled((<<[Int], <<[Int], <<[Int], <<[Int]))
  }
  /** Table description of table serverskill. Objects of this class serve as prototypes for rows in queries.
   *  NOTE: The following names collided with Scala keywords and were escaped: type */
  class Serverskill(tag: Tag) extends Table[ServerskillRow](tag, "serverskill") {
    def * = (`type`, level, needExp, needLevel) <> (ServerskillRow.tupled, ServerskillRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (`type`.?, level.?, needExp.?, needLevel.?).shaped.<>({r=>import r._; _1.map(_=> ServerskillRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column type Default(0)
     *  NOTE: The name was escaped because it collided with a Scala keyword. */
    val `type`: Column[Int] = column[Int]("type", O.Default(0))
    /** Database column level Default(0) */
    val level: Column[Int] = column[Int]("level", O.Default(0))
    /** Database column need_exp Default(0) */
    val needExp: Column[Int] = column[Int]("need_exp", O.Default(0))
    /** Database column need_level Default(0) */
    val needLevel: Column[Int] = column[Int]("need_level", O.Default(0))
  }
  /** Collection-like TableQuery object for table Serverskill */
  lazy val Serverskill = new TableQuery(tag => new Serverskill(tag))
  
  /** Entity class storing rows of table Shops
   *  @param shopid Database column ShopID PrimaryKey
   *  @param itemid Database column ItemID  */
  case class ShopsRow(shopid: Option[Int], itemid: Option[Int])
  /** GetResult implicit for fetching ShopsRow objects using plain SQL queries */
  implicit def GetResultShopsRow(implicit e0: GR[Option[Int]]): GR[ShopsRow] = GR{
    prs => import prs._
    ShopsRow.tupled((<<?[Int], <<?[Int]))
  }
  /** Table description of table shops. Objects of this class serve as prototypes for rows in queries. */
  class Shops(tag: Tag) extends Table[ShopsRow](tag, "shops") {
    def * = (shopid, itemid) <> (ShopsRow.tupled, ShopsRow.unapply)
    
    /** Database column ShopID PrimaryKey */
    val shopid: Column[Option[Int]] = column[Option[Int]]("ShopID", O.PrimaryKey)
    /** Database column ItemID  */
    val itemid: Column[Option[Int]] = column[Option[Int]]("ItemID")
    
    /** Index over (shopid) (database name pri) */
    val index1 = index("pri", shopid)
  }
  /** Collection-like TableQuery object for table Shops */
  lazy val Shops = new TableQuery(tag => new Shops(tag))
  
  /** Entity class storing rows of table Skills
   *  @param charid Database column CharID Default(0)
   *  @param skillid Database column SkillID Default(0)
   *  @param skilllevel Database column SkillLevel Default(0)
   *  @param skillexp Database column SkillExp Default(0) */
  case class SkillsRow(charid: Int = 0, skillid: Int = 0, skilllevel: Int = 0, skillexp: Int = 0)
  /** GetResult implicit for fetching SkillsRow objects using plain SQL queries */
  implicit def GetResultSkillsRow(implicit e0: GR[Int]): GR[SkillsRow] = GR{
    prs => import prs._
    SkillsRow.tupled((<<[Int], <<[Int], <<[Int], <<[Int]))
  }
  /** Table description of table skills. Objects of this class serve as prototypes for rows in queries. */
  class Skills(tag: Tag) extends Table[SkillsRow](tag, "skills") {
    def * = (charid, skillid, skilllevel, skillexp) <> (SkillsRow.tupled, SkillsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (charid.?, skillid.?, skilllevel.?, skillexp.?).shaped.<>({r=>import r._; _1.map(_=> SkillsRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column CharID Default(0) */
    val charid: Column[Int] = column[Int]("CharID", O.Default(0))
    /** Database column SkillID Default(0) */
    val skillid: Column[Int] = column[Int]("SkillID", O.Default(0))
    /** Database column SkillLevel Default(0) */
    val skilllevel: Column[Int] = column[Int]("SkillLevel", O.Default(0))
    /** Database column SkillExp Default(0) */
    val skillexp: Column[Int] = column[Int]("SkillExp", O.Default(0))
  }
  /** Collection-like TableQuery object for table Skills */
  lazy val Skills = new TableQuery(tag => new Skills(tag))
  
  /** Entity class storing rows of table Stats
   *  @param profession Database column Profession 
   *  @param level Database column Level 
   *  @param strength Database column Strength 
   *  @param vitality Database column Vitality 
   *  @param agility Database column Agility 
   *  @param spirit Database column Spirit  */
  case class StatsRow(profession: String, level: Int, strength: Int, vitality: Int, agility: Int, spirit: Int)
  /** GetResult implicit for fetching StatsRow objects using plain SQL queries */
  implicit def GetResultStatsRow(implicit e0: GR[String], e1: GR[Int]): GR[StatsRow] = GR{
    prs => import prs._
    StatsRow.tupled((<<[String], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int]))
  }
  /** Table description of table stats. Objects of this class serve as prototypes for rows in queries. */
  class Stats(tag: Tag) extends Table[StatsRow](tag, "stats") {
    def * = (profession, level, strength, vitality, agility, spirit) <> (StatsRow.tupled, StatsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (profession.?, level.?, strength.?, vitality.?, agility.?, spirit.?).shaped.<>({r=>import r._; _1.map(_=> StatsRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column Profession  */
    val profession: Column[String] = column[String]("Profession")
    /** Database column Level  */
    val level: Column[Int] = column[Int]("Level")
    /** Database column Strength  */
    val strength: Column[Int] = column[Int]("Strength")
    /** Database column Vitality  */
    val vitality: Column[Int] = column[Int]("Vitality")
    /** Database column Agility  */
    val agility: Column[Int] = column[Int]("Agility")
    /** Database column Spirit  */
    val spirit: Column[Int] = column[Int]("Spirit")
    
    /** Primary key of Stats (database name stats_PK) */
    val pk = primaryKey("stats_PK", (profession, level))
  }
  /** Collection-like TableQuery object for table Stats */
  lazy val Stats = new TableQuery(tag => new Stats(tag))
  
  /** Entity class storing rows of table Tnpcs
   *  @param uid Database column UID AutoInc, PrimaryKey
   *  @param `type` Database column Type Default(0)
   *  @param flags Database column Flags Default(0)
   *  @param direction Database column Direction Default(0)
   *  @param x Database column X Default(0)
   *  @param y Database column Y Default(0)
   *  @param map Database column Map Default(0) */
  case class TnpcsRow(uid: Int, `type`: Int = 0, flags: Int = 0, direction: Int = 0, x: Int = 0, y: Int = 0, map: Int = 0)
  /** GetResult implicit for fetching TnpcsRow objects using plain SQL queries */
  implicit def GetResultTnpcsRow(implicit e0: GR[Int]): GR[TnpcsRow] = GR{
    prs => import prs._
    TnpcsRow.tupled((<<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int]))
  }
  /** Table description of table tnpcs. Objects of this class serve as prototypes for rows in queries.
   *  NOTE: The following names collided with Scala keywords and were escaped: type */
  class Tnpcs(tag: Tag) extends Table[TnpcsRow](tag, "tnpcs") {
    def * = (uid, `type`, flags, direction, x, y, map) <> (TnpcsRow.tupled, TnpcsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (uid.?, `type`.?, flags.?, direction.?, x.?, y.?, map.?).shaped.<>({r=>import r._; _1.map(_=> TnpcsRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column UID AutoInc, PrimaryKey */
    val uid: Column[Int] = column[Int]("UID", O.AutoInc, O.PrimaryKey)
    /** Database column Type Default(0)
     *  NOTE: The name was escaped because it collided with a Scala keyword. */
    val `type`: Column[Int] = column[Int]("Type", O.Default(0))
    /** Database column Flags Default(0) */
    val flags: Column[Int] = column[Int]("Flags", O.Default(0))
    /** Database column Direction Default(0) */
    val direction: Column[Int] = column[Int]("Direction", O.Default(0))
    /** Database column X Default(0) */
    val x: Column[Int] = column[Int]("X", O.Default(0))
    /** Database column Y Default(0) */
    val y: Column[Int] = column[Int]("Y", O.Default(0))
    /** Database column Map Default(0) */
    val map: Column[Int] = column[Int]("Map", O.Default(0))
  }
  /** Collection-like TableQuery object for table Tnpcs */
  lazy val Tnpcs = new TableQuery(tag => new Tnpcs(tag))
  
  /** Row type of table Tqnpcs */
  type TqnpcsRow = HCons[Int,HCons[Int,HCons[Option[Int],HCons[Option[Int],HCons[Option[String],HCons[Option[Int],HCons[Option[Int],HCons[Option[Int],HCons[Option[Int],HCons[Option[Int],HCons[Option[Int],HCons[Option[Int],HCons[Option[Int],HCons[Option[Int],HCons[Option[Int],HCons[Option[Int],HCons[Option[Int],HCons[Option[Int],HCons[Option[Int],HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Option[String],HCons[Int,HCons[Short,HCons[Short,HCons[Int,HCons[Short,HCons[Option[Int],HNil]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]
  /** Constructor for TqnpcsRow providing default values if available in the database schema. */
  def TqnpcsRow(npcid: Int, npctype: Int = 0, ownerid: Option[Int] = Some(0), playerid: Option[Int] = Some(0), name: Option[String] = None, notnpctype: Option[Int] = Some(0), subtype: Option[Int] = Some(0), idxserver: Option[Int] = Some(-1), mapid: Option[Int] = Some(0), xcord: Option[Int] = Some(0), ycord: Option[Int] = Some(0), task0nottype: Option[Int] = Some(0), task1: Option[Int] = Some(0), task2: Option[Int] = Some(0), task3: Option[Int] = Some(0), task4: Option[Int] = Some(0), task5: Option[Int] = Some(0), task6: Option[Int] = Some(0), task7: Option[Int] = Some(0), data0: Int = 0, data1: Int = 0, data2: Int = 0, data3: Int = 0, datastr: Option[String] = None, linkid: Int = 0, life: Short = 0, maxlife: Short = 0, direction: Int = 0, flag: Short = 0, itemid: Option[Int]): TqnpcsRow = {
    npcid :: npctype :: ownerid :: playerid :: name :: notnpctype :: subtype :: idxserver :: mapid :: xcord :: ycord :: task0nottype :: task1 :: task2 :: task3 :: task4 :: task5 :: task6 :: task7 :: data0 :: data1 :: data2 :: data3 :: datastr :: linkid :: life :: maxlife :: direction :: flag :: itemid :: HNil
  }
  /** GetResult implicit for fetching TqnpcsRow objects using plain SQL queries */
  implicit def GetResultTqnpcsRow(implicit e0: GR[Int], e1: GR[Option[Int]], e2: GR[Option[String]], e3: GR[Short]): GR[TqnpcsRow] = GR{
    prs => import prs._
    <<[Int] :: <<[Int] :: <<?[Int] :: <<?[Int] :: <<?[String] :: <<?[Int] :: <<?[Int] :: <<?[Int] :: <<?[Int] :: <<?[Int] :: <<?[Int] :: <<?[Int] :: <<?[Int] :: <<?[Int] :: <<?[Int] :: <<?[Int] :: <<?[Int] :: <<?[Int] :: <<?[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<?[String] :: <<[Int] :: <<[Short] :: <<[Short] :: <<[Int] :: <<[Short] :: <<?[Int] :: HNil
  }
  /** Table description of table tqnpcs. Objects of this class serve as prototypes for rows in queries. */
  class Tqnpcs(tag: Tag) extends Table[TqnpcsRow](tag, "tqnpcs") {
    def * = npcid :: npctype :: ownerid :: playerid :: name :: notnpctype :: subtype :: idxserver :: mapid :: xcord :: ycord :: task0nottype :: task1 :: task2 :: task3 :: task4 :: task5 :: task6 :: task7 :: data0 :: data1 :: data2 :: data3 :: datastr :: linkid :: life :: maxlife :: direction :: flag :: itemid :: HNil
    
    /** Database column NpcID AutoInc, PrimaryKey */
    val npcid: Column[Int] = column[Int]("NpcID", O.AutoInc, O.PrimaryKey)
    /** Database column NpcType Default(0) */
    val npctype: Column[Int] = column[Int]("NpcType", O.Default(0))
    /** Database column ownerid Default(Some(0)) */
    val ownerid: Column[Option[Int]] = column[Option[Int]]("ownerid", O.Default(Some(0)))
    /** Database column playerid Default(Some(0)) */
    val playerid: Column[Option[Int]] = column[Option[Int]]("playerid", O.Default(Some(0)))
    /** Database column name Default(None) */
    val name: Column[Option[String]] = column[Option[String]]("name", O.Default(None))
    /** Database column NotNpcType Default(Some(0)) */
    val notnpctype: Column[Option[Int]] = column[Option[Int]]("NotNpcType", O.Default(Some(0)))
    /** Database column SubType Default(Some(0)) */
    val subtype: Column[Option[Int]] = column[Option[Int]]("SubType", O.Default(Some(0)))
    /** Database column idxserver Default(Some(-1)) */
    val idxserver: Column[Option[Int]] = column[Option[Int]]("idxserver", O.Default(Some(-1)))
    /** Database column MapID Default(Some(0)) */
    val mapid: Column[Option[Int]] = column[Option[Int]]("MapID", O.Default(Some(0)))
    /** Database column Xcord Default(Some(0)) */
    val xcord: Column[Option[Int]] = column[Option[Int]]("Xcord", O.Default(Some(0)))
    /** Database column Ycord Default(Some(0)) */
    val ycord: Column[Option[Int]] = column[Option[Int]]("Ycord", O.Default(Some(0)))
    /** Database column task0nottype Default(Some(0)) */
    val task0nottype: Column[Option[Int]] = column[Option[Int]]("task0nottype", O.Default(Some(0)))
    /** Database column task1 Default(Some(0)) */
    val task1: Column[Option[Int]] = column[Option[Int]]("task1", O.Default(Some(0)))
    /** Database column task2 Default(Some(0)) */
    val task2: Column[Option[Int]] = column[Option[Int]]("task2", O.Default(Some(0)))
    /** Database column task3 Default(Some(0)) */
    val task3: Column[Option[Int]] = column[Option[Int]]("task3", O.Default(Some(0)))
    /** Database column task4 Default(Some(0)) */
    val task4: Column[Option[Int]] = column[Option[Int]]("task4", O.Default(Some(0)))
    /** Database column task5 Default(Some(0)) */
    val task5: Column[Option[Int]] = column[Option[Int]]("task5", O.Default(Some(0)))
    /** Database column task6 Default(Some(0)) */
    val task6: Column[Option[Int]] = column[Option[Int]]("task6", O.Default(Some(0)))
    /** Database column task7 Default(Some(0)) */
    val task7: Column[Option[Int]] = column[Option[Int]]("task7", O.Default(Some(0)))
    /** Database column data0 Default(0) */
    val data0: Column[Int] = column[Int]("data0", O.Default(0))
    /** Database column data1 Default(0) */
    val data1: Column[Int] = column[Int]("data1", O.Default(0))
    /** Database column data2 Default(0) */
    val data2: Column[Int] = column[Int]("data2", O.Default(0))
    /** Database column data3 Default(0) */
    val data3: Column[Int] = column[Int]("data3", O.Default(0))
    /** Database column datastr Default(None) */
    val datastr: Column[Option[String]] = column[Option[String]]("datastr", O.Default(None))
    /** Database column linkid Default(0) */
    val linkid: Column[Int] = column[Int]("linkid", O.Default(0))
    /** Database column life Default(0) */
    val life: Column[Short] = column[Short]("life", O.Default(0))
    /** Database column maxlife Default(0) */
    val maxlife: Column[Short] = column[Short]("maxlife", O.Default(0))
    /** Database column Direction Default(0) */
    val direction: Column[Int] = column[Int]("Direction", O.Default(0))
    /** Database column Flag Default(0) */
    val flag: Column[Short] = column[Short]("Flag", O.Default(0))
    /** Database column itemid  */
    val itemid: Column[Option[Int]] = column[Option[Int]]("itemid")
  }
  /** Collection-like TableQuery object for table Tqnpcs */
  lazy val Tqnpcs = new TableQuery(tag => new Tqnpcs(tag))
}