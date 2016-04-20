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
  lazy val ddl = Accounts.ddl ++ CharacterGuild.ddl ++ Characters.ddl ++ Enemys.ddl ++ Friends.ddl ++ Guilds.ddl ++ Items.ddl ++ Maps.ddl ++ Monsters.ddl ++ Monsterspawns.ddl ++ Poleholder.ddl ++ Portals.ddl ++ Prof.ddl ++ Revpoints.ddl ++ Serverskill.ddl ++ Shops.ddl ++ Skills.ddl ++ Stats.ddl ++ Tqnpcs.ddl ++ Warehouses.ddl
  
  /** Entity class storing rows of table Accounts
   *  @param id Database column id AutoInc, PrimaryKey
   *  @param accountid Database column AccountID 
   *  @param password Database column Password  */
  case class AccountsRow(id: Int, accountid: Option[String], password: Option[String])
  /** GetResult implicit for fetching AccountsRow objects using plain SQL queries */
  implicit def GetResultAccountsRow(implicit e0: GR[Int], e1: GR[Option[String]]): GR[AccountsRow] = GR{
    prs => import prs._
    AccountsRow.tupled((<<[Int], <<?[String], <<?[String]))
  }
  /** Table description of table accounts. Objects of this class serve as prototypes for rows in queries. */
  class Accounts(tag: Tag) extends Table[AccountsRow](tag, "accounts") {
    def * = (id, accountid, password) <> (AccountsRow.tupled, AccountsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, accountid, password).shaped.<>({r=>import r._; _1.map(_=> AccountsRow.tupled((_1.get, _2, _3)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id AutoInc, PrimaryKey */
    val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column AccountID  */
    val accountid: Column[Option[String]] = column[Option[String]]("AccountID")
    /** Database column Password  */
    val password: Column[Option[String]] = column[Option[String]]("Password")
    
    /** Uniqueness Index over (accountid) (database name AccountID) */
    val index1 = index("AccountID", accountid, unique=true)
  }
  /** Collection-like TableQuery object for table Accounts */
  lazy val Accounts = new TableQuery(tag => new Accounts(tag))
  
  /** Entity class storing rows of table CharacterGuild
   *  @param charid Database column charID PrimaryKey
   *  @param guildid Database column guildID 
   *  @param donation Database column donation Default(0)
   *  @param rank Database column rank Default(50) */
  case class CharacterGuildRow(charid: Int, guildid: Int, donation: Int = 0, rank: Int = 50)
  /** GetResult implicit for fetching CharacterGuildRow objects using plain SQL queries */
  implicit def GetResultCharacterGuildRow(implicit e0: GR[Int]): GR[CharacterGuildRow] = GR{
    prs => import prs._
    CharacterGuildRow.tupled((<<[Int], <<[Int], <<[Int], <<[Int]))
  }
  /** Table description of table character_guild. Objects of this class serve as prototypes for rows in queries. */
  class CharacterGuild(tag: Tag) extends Table[CharacterGuildRow](tag, "character_guild") {
    def * = (charid, guildid, donation, rank) <> (CharacterGuildRow.tupled, CharacterGuildRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (charid.?, guildid.?, donation.?, rank.?).shaped.<>({r=>import r._; _1.map(_=> CharacterGuildRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column charID PrimaryKey */
    val charid: Column[Int] = column[Int]("charID", O.PrimaryKey)
    /** Database column guildID  */
    val guildid: Column[Int] = column[Int]("guildID")
    /** Database column donation Default(0) */
    val donation: Column[Int] = column[Int]("donation", O.Default(0))
    /** Database column rank Default(50) */
    val rank: Column[Int] = column[Int]("rank", O.Default(50))
    
    /** Foreign key referencing Characters (database name f_char_guild_charID) */
    lazy val charactersFk = foreignKey("f_char_guild_charID", charid, Characters)(r => r.charid, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
    /** Foreign key referencing Guilds (database name f_char_guild_guildID) */
    lazy val guildsFk = foreignKey("f_char_guild_guildID", guildid, Guilds)(r => r.guildid, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table CharacterGuild */
  lazy val CharacterGuild = new TableQuery(tag => new CharacterGuild(tag))
  
  /** Row type of table Characters */
  type CharactersRow = HCons[Int,HCons[String,HCons[String,HCons[String,HCons[String,HCons[Int,HCons[Long,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[String,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Option[java.sql.Timestamp],HCons[Option[Int],HCons[Option[Int],HCons[Option[Int],HCons[Option[Int],HCons[Option[Int],HNil]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]
  /** Constructor for CharactersRow providing default values if available in the database schema. */
  def CharactersRow(charid: Int, name: String, account: String, server: String, spouse: String, level: Int = 1, exp: Long = 0L, str: Int = 0, dex: Int = 0, spi: Int = 0, vit: Int = 0, hp: Int = 1, mp: Int = 0, pkpoints: Int = 0, statpoints: Int = 0, money: Int = 0, cpoints: Int = 0, vpoints: Int = 0, whmoney: Int = 0, hairstyle: Int = 0, model: Int = 0, map: Int = 1002, mapinstance: Int = 0, xcord: Int = 438, ycord: Int = 377, status: String, reborn: Int = 0, isgm: Int = 0, nobility: Int = 0, `class`: Int = 0, ispm: Int = 0, firstlog: Int = 0, dbexpused: Option[java.sql.Timestamp] = None, exppotiontime: Option[Int] = Some(0), exppotionrate: Option[Int] = Some(0), previousmap: Option[Int] = Some(1002), houseid: Option[Int] = Some(0), housetype: Option[Int] = Some(0)): CharactersRow = {
    charid :: name :: account :: server :: spouse :: level :: exp :: str :: dex :: spi :: vit :: hp :: mp :: pkpoints :: statpoints :: money :: cpoints :: vpoints :: whmoney :: hairstyle :: model :: map :: mapinstance :: xcord :: ycord :: status :: reborn :: isgm :: nobility :: `class` :: ispm :: firstlog :: dbexpused :: exppotiontime :: exppotionrate :: previousmap :: houseid :: housetype :: HNil
  }
  /** GetResult implicit for fetching CharactersRow objects using plain SQL queries */
  implicit def GetResultCharactersRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Long], e3: GR[Option[java.sql.Timestamp]], e4: GR[Option[Int]]): GR[CharactersRow] = GR{
    prs => import prs._
    <<[Int] :: <<[String] :: <<[String] :: <<[String] :: <<[String] :: <<[Int] :: <<[Long] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[String] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<?[java.sql.Timestamp] :: <<?[Int] :: <<?[Int] :: <<?[Int] :: <<?[Int] :: <<?[Int] :: HNil
  }
  /** Table description of table characters. Objects of this class serve as prototypes for rows in queries.
   *  NOTE: The following names collided with Scala keywords and were escaped: class */
  class Characters(tag: Tag) extends Table[CharactersRow](tag, "characters") {
    def * = charid :: name :: account :: server :: spouse :: level :: exp :: str :: dex :: spi :: vit :: hp :: mp :: pkpoints :: statpoints :: money :: cpoints :: vpoints :: whmoney :: hairstyle :: model :: map :: mapinstance :: xcord :: ycord :: status :: reborn :: isgm :: nobility :: `class` :: ispm :: firstlog :: dbexpused :: exppotiontime :: exppotionrate :: previousmap :: houseid :: housetype :: HNil
    
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
    /** Database column Reborn Default(0) */
    val reborn: Column[Int] = column[Int]("Reborn", O.Default(0))
    /** Database column isGM Default(0) */
    val isgm: Column[Int] = column[Int]("isGM", O.Default(0))
    /** Database column nobility Default(0) */
    val nobility: Column[Int] = column[Int]("nobility", O.Default(0))
    /** Database column Class Default(0)
     *  NOTE: The name was escaped because it collided with a Scala keyword. */
    val `class`: Column[Int] = column[Int]("Class", O.Default(0))
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
   *  @param id Database column id AutoInc, PrimaryKey
   *  @param charid Database column charID 
   *  @param friendid Database column friendID 
   *  @param friendname Database column friendName  */
  case class FriendsRow(id: Int, charid: Int, friendid: Int, friendname: String)
  /** GetResult implicit for fetching FriendsRow objects using plain SQL queries */
  implicit def GetResultFriendsRow(implicit e0: GR[Int], e1: GR[String]): GR[FriendsRow] = GR{
    prs => import prs._
    FriendsRow.tupled((<<[Int], <<[Int], <<[Int], <<[String]))
  }
  /** Table description of table friends. Objects of this class serve as prototypes for rows in queries. */
  class Friends(tag: Tag) extends Table[FriendsRow](tag, "friends") {
    def * = (id, charid, friendid, friendname) <> (FriendsRow.tupled, FriendsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, charid.?, friendid.?, friendname.?).shaped.<>({r=>import r._; _1.map(_=> FriendsRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id AutoInc, PrimaryKey */
    val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column charID  */
    val charid: Column[Int] = column[Int]("charID")
    /** Database column friendID  */
    val friendid: Column[Int] = column[Int]("friendID")
    /** Database column friendName  */
    val friendname: Column[String] = column[String]("friendName")
    
    /** Foreign key referencing Characters (database name f_charID) */
    lazy val charactersFk1 = foreignKey("f_charID", charid, Characters)(r => r.charid, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Characters (database name f_friendID) */
    lazy val charactersFk2 = foreignKey("f_friendID", friendid, Characters)(r => r.charid, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Friends */
  lazy val Friends = new TableQuery(tag => new Friends(tag))
  
  /** Entity class storing rows of table Guilds
   *  @param guildid Database column GuildID AutoInc, PrimaryKey
   *  @param name Database column Name 
   *  @param leader Database column Leader 
   *  @param fund Database column Fund Default(0)
   *  @param bulletin Database column Bulletin  */
  case class GuildsRow(guildid: Int, name: String, leader: Int, fund: Int = 0, bulletin: String)
  /** GetResult implicit for fetching GuildsRow objects using plain SQL queries */
  implicit def GetResultGuildsRow(implicit e0: GR[Int], e1: GR[String]): GR[GuildsRow] = GR{
    prs => import prs._
    GuildsRow.tupled((<<[Int], <<[String], <<[Int], <<[Int], <<[String]))
  }
  /** Table description of table guilds. Objects of this class serve as prototypes for rows in queries. */
  class Guilds(tag: Tag) extends Table[GuildsRow](tag, "guilds") {
    def * = (guildid, name, leader, fund, bulletin) <> (GuildsRow.tupled, GuildsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (guildid.?, name.?, leader.?, fund.?, bulletin.?).shaped.<>({r=>import r._; _1.map(_=> GuildsRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column GuildID AutoInc, PrimaryKey */
    val guildid: Column[Int] = column[Int]("GuildID", O.AutoInc, O.PrimaryKey)
    /** Database column Name  */
    val name: Column[String] = column[String]("Name")
    /** Database column Leader  */
    val leader: Column[Int] = column[Int]("Leader")
    /** Database column Fund Default(0) */
    val fund: Column[Int] = column[Int]("Fund", O.Default(0))
    /** Database column Bulletin  */
    val bulletin: Column[String] = column[String]("Bulletin")
    
    /** Foreign key referencing Characters (database name f_leader) */
    lazy val charactersFk = foreignKey("f_leader", leader, Characters)(r => r.charid, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
    
    /** Uniqueness Index over (leader) (database name Leader_UNIQUE) */
    val index1 = index("Leader_UNIQUE", leader, unique=true)
    /** Uniqueness Index over (name) (database name Name_UNIQUE) */
    val index2 = index("Name_UNIQUE", name, unique=true)
  }
  /** Collection-like TableQuery object for table Guilds */
  lazy val Guilds = new TableQuery(tag => new Guilds(tag))
  
  /** Entity class storing rows of table Items
   *  @param charid Database column CharID Default(0)
   *  @param itemuid Database column ItemUID PrimaryKey
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
    /** Database column ItemUID PrimaryKey */
    val itemuid: Column[Int] = column[Int]("ItemUID", O.PrimaryKey)
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
    
    /** Foreign key referencing Characters (database name charid) */
    lazy val charactersFk = foreignKey("charid", charid, Characters)(r => r.charid, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
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
  
  /** Entity class storing rows of table Monsters
   *  @param id Database column Id Default(0), PrimaryKey
   *  @param name Database column Name 
   *  @param mesh Database column Mesh 
   *  @param level Database column Level 
   *  @param hitpoints Database column Hitpoints 
   *  @param minattack Database column MinAttack 
   *  @param maxattack Database column MaxAttack 
   *  @param magicdefence Database column MagicDefence 
   *  @param defence Database column Defence 
   *  @param attackrange Database column AttackRange 
   *  @param viewdistance Database column ViewDistance 
   *  @param dropmoney Database column DropMoney 
   *  @param speed Database column Speed 
   *  @param dodge Database column Dodge 
   *  @param attacktype Database column AttackType  */
  case class MonstersRow(id: Int = 0, name: String, mesh: Int, level: Int, hitpoints: Int, minattack: Int, maxattack: Int, magicdefence: Int, defence: Int, attackrange: Int, viewdistance: Int, dropmoney: Int, speed: Int, dodge: Int, attacktype: Int)
  /** GetResult implicit for fetching MonstersRow objects using plain SQL queries */
  implicit def GetResultMonstersRow(implicit e0: GR[Int], e1: GR[String]): GR[MonstersRow] = GR{
    prs => import prs._
    MonstersRow.tupled((<<[Int], <<[String], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int]))
  }
  /** Table description of table monsters. Objects of this class serve as prototypes for rows in queries. */
  class Monsters(tag: Tag) extends Table[MonstersRow](tag, "monsters") {
    def * = (id, name, mesh, level, hitpoints, minattack, maxattack, magicdefence, defence, attackrange, viewdistance, dropmoney, speed, dodge, attacktype) <> (MonstersRow.tupled, MonstersRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?, mesh.?, level.?, hitpoints.?, minattack.?, maxattack.?, magicdefence.?, defence.?, attackrange.?, viewdistance.?, dropmoney.?, speed.?, dodge.?, attacktype.?).shaped.<>({r=>import r._; _1.map(_=> MonstersRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get, _12.get, _13.get, _14.get, _15.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column Id Default(0), PrimaryKey */
    val id: Column[Int] = column[Int]("Id", O.Default(0), O.PrimaryKey)
    /** Database column Name  */
    val name: Column[String] = column[String]("Name")
    /** Database column Mesh  */
    val mesh: Column[Int] = column[Int]("Mesh")
    /** Database column Level  */
    val level: Column[Int] = column[Int]("Level")
    /** Database column Hitpoints  */
    val hitpoints: Column[Int] = column[Int]("Hitpoints")
    /** Database column MinAttack  */
    val minattack: Column[Int] = column[Int]("MinAttack")
    /** Database column MaxAttack  */
    val maxattack: Column[Int] = column[Int]("MaxAttack")
    /** Database column MagicDefence  */
    val magicdefence: Column[Int] = column[Int]("MagicDefence")
    /** Database column Defence  */
    val defence: Column[Int] = column[Int]("Defence")
    /** Database column AttackRange  */
    val attackrange: Column[Int] = column[Int]("AttackRange")
    /** Database column ViewDistance  */
    val viewdistance: Column[Int] = column[Int]("ViewDistance")
    /** Database column DropMoney  */
    val dropmoney: Column[Int] = column[Int]("DropMoney")
    /** Database column Speed  */
    val speed: Column[Int] = column[Int]("Speed")
    /** Database column Dodge  */
    val dodge: Column[Int] = column[Int]("Dodge")
    /** Database column AttackType  */
    val attacktype: Column[Int] = column[Int]("AttackType")
  }
  /** Collection-like TableQuery object for table Monsters */
  lazy val Monsters = new TableQuery(tag => new Monsters(tag))
  
  /** Entity class storing rows of table Monsterspawns
   *  @param id Database column Id Default(1)
   *  @param xstart Database column XStart 
   *  @param ystart Database column YStart 
   *  @param uniqueid Database column UniqueID AutoInc, PrimaryKey
   *  @param mapid Database column MapID 
   *  @param xstop Database column XStop 
   *  @param ystop Database column YStop 
   *  @param numbertospawn Database column NumberToSpawn Default(1) */
  case class MonsterspawnsRow(id: Int = 1, xstart: Int, ystart: Int, uniqueid: Int, mapid: Int, xstop: Int, ystop: Int, numbertospawn: Int = 1)
  /** GetResult implicit for fetching MonsterspawnsRow objects using plain SQL queries */
  implicit def GetResultMonsterspawnsRow(implicit e0: GR[Int]): GR[MonsterspawnsRow] = GR{
    prs => import prs._
    MonsterspawnsRow.tupled((<<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int]))
  }
  /** Table description of table monsterspawns. Objects of this class serve as prototypes for rows in queries. */
  class Monsterspawns(tag: Tag) extends Table[MonsterspawnsRow](tag, "monsterspawns") {
    def * = (id, xstart, ystart, uniqueid, mapid, xstop, ystop, numbertospawn) <> (MonsterspawnsRow.tupled, MonsterspawnsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, xstart.?, ystart.?, uniqueid.?, mapid.?, xstop.?, ystop.?, numbertospawn.?).shaped.<>({r=>import r._; _1.map(_=> MonsterspawnsRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column Id Default(1) */
    val id: Column[Int] = column[Int]("Id", O.Default(1))
    /** Database column XStart  */
    val xstart: Column[Int] = column[Int]("XStart")
    /** Database column YStart  */
    val ystart: Column[Int] = column[Int]("YStart")
    /** Database column UniqueID AutoInc, PrimaryKey */
    val uniqueid: Column[Int] = column[Int]("UniqueID", O.AutoInc, O.PrimaryKey)
    /** Database column MapID  */
    val mapid: Column[Int] = column[Int]("MapID")
    /** Database column XStop  */
    val xstop: Column[Int] = column[Int]("XStop")
    /** Database column YStop  */
    val ystop: Column[Int] = column[Int]("YStop")
    /** Database column NumberToSpawn Default(1) */
    val numbertospawn: Column[Int] = column[Int]("NumberToSpawn", O.Default(1))
  }
  /** Collection-like TableQuery object for table Monsterspawns */
  lazy val Monsterspawns = new TableQuery(tag => new Monsterspawns(tag))
  
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
  
  /** Entity class storing rows of table Portals
   *  @param id Database column id AutoInc, PrimaryKey
   *  @param fromMap Database column from_map 
   *  @param fromX Database column from_x 
   *  @param fromY Database column from_y 
   *  @param toMap Database column to_map 
   *  @param toX Database column to_x 
   *  @param toY Database column to_y  */
  case class PortalsRow(id: Int, fromMap: Int, fromX: Int, fromY: Int, toMap: Int, toX: Int, toY: Int)
  /** GetResult implicit for fetching PortalsRow objects using plain SQL queries */
  implicit def GetResultPortalsRow(implicit e0: GR[Int]): GR[PortalsRow] = GR{
    prs => import prs._
    PortalsRow.tupled((<<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int]))
  }
  /** Table description of table portals. Objects of this class serve as prototypes for rows in queries. */
  class Portals(tag: Tag) extends Table[PortalsRow](tag, "portals") {
    def * = (id, fromMap, fromX, fromY, toMap, toX, toY) <> (PortalsRow.tupled, PortalsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, fromMap.?, fromX.?, fromY.?, toMap.?, toX.?, toY.?).shaped.<>({r=>import r._; _1.map(_=> PortalsRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id AutoInc, PrimaryKey */
    val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column from_map  */
    val fromMap: Column[Int] = column[Int]("from_map")
    /** Database column from_x  */
    val fromX: Column[Int] = column[Int]("from_x")
    /** Database column from_y  */
    val fromY: Column[Int] = column[Int]("from_y")
    /** Database column to_map  */
    val toMap: Column[Int] = column[Int]("to_map")
    /** Database column to_x  */
    val toX: Column[Int] = column[Int]("to_x")
    /** Database column to_y  */
    val toY: Column[Int] = column[Int]("to_y")
  }
  /** Collection-like TableQuery object for table Portals */
  lazy val Portals = new TableQuery(tag => new Portals(tag))
  
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
  case class ShopsRow(shopid: Int, itemid: Int)
  /** GetResult implicit for fetching ShopsRow objects using plain SQL queries */
  implicit def GetResultShopsRow(implicit e0: GR[Int]): GR[ShopsRow] = GR{
    prs => import prs._
    ShopsRow.tupled((<<[Int], <<[Int]))
  }
  /** Table description of table shops. Objects of this class serve as prototypes for rows in queries. */
  class Shops(tag: Tag) extends Table[ShopsRow](tag, "shops") {
    def * = (shopid, itemid) <> (ShopsRow.tupled, ShopsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (shopid.?, itemid.?).shaped.<>({r=>import r._; _1.map(_=> ShopsRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ShopID PrimaryKey */
    val shopid: Column[Int] = column[Int]("ShopID", O.PrimaryKey)
    /** Database column ItemID  */
    val itemid: Column[Int] = column[Int]("ItemID")
    
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
  
  /** Row type of table Tqnpcs */
  type TqnpcsRow = HCons[Int,HCons[Int,HCons[Option[Int],HCons[Option[Int],HCons[Option[String],HCons[Option[Int],HCons[Option[Int],HCons[Option[Int],HCons[Option[Int],HCons[Option[Int],HCons[Option[Int],HCons[Option[Int],HCons[Option[Int],HCons[Option[Int],HCons[Option[Int],HCons[Option[Int],HCons[Option[Int],HCons[Option[Int],HCons[Option[Int],HCons[Int,HCons[Int,HCons[Int,HCons[Int,HCons[Option[String],HCons[Int,HCons[Short,HCons[Short,HCons[Int,HCons[Short,HCons[Option[Int],HCons[Int,HNil]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]
  /** Constructor for TqnpcsRow providing default values if available in the database schema. */
  def TqnpcsRow(npcid: Int, npctype: Int = 0, ownerid: Option[Int] = Some(0), playerid: Option[Int] = Some(0), name: Option[String] = None, notnpctype: Option[Int] = Some(0), subtype: Option[Int] = Some(0), idxserver: Option[Int] = Some(-1), mapid: Option[Int] = Some(0), xcord: Option[Int] = Some(0), ycord: Option[Int] = Some(0), task0nottype: Option[Int] = Some(0), task1: Option[Int] = Some(0), task2: Option[Int] = Some(0), task3: Option[Int] = Some(0), task4: Option[Int] = Some(0), task5: Option[Int] = Some(0), task6: Option[Int] = Some(0), task7: Option[Int] = Some(0), data0: Int = 0, data1: Int = 0, data2: Int = 0, data3: Int = 0, datastr: Option[String] = None, linkid: Int = 0, life: Short = 0, maxlife: Short = 0, direction: Int = 0, flag: Short = 0, itemid: Option[Int], face: Int = 1): TqnpcsRow = {
    npcid :: npctype :: ownerid :: playerid :: name :: notnpctype :: subtype :: idxserver :: mapid :: xcord :: ycord :: task0nottype :: task1 :: task2 :: task3 :: task4 :: task5 :: task6 :: task7 :: data0 :: data1 :: data2 :: data3 :: datastr :: linkid :: life :: maxlife :: direction :: flag :: itemid :: face :: HNil
  }
  /** GetResult implicit for fetching TqnpcsRow objects using plain SQL queries */
  implicit def GetResultTqnpcsRow(implicit e0: GR[Int], e1: GR[Option[Int]], e2: GR[Option[String]], e3: GR[Short]): GR[TqnpcsRow] = GR{
    prs => import prs._
    <<[Int] :: <<[Int] :: <<?[Int] :: <<?[Int] :: <<?[String] :: <<?[Int] :: <<?[Int] :: <<?[Int] :: <<?[Int] :: <<?[Int] :: <<?[Int] :: <<?[Int] :: <<?[Int] :: <<?[Int] :: <<?[Int] :: <<?[Int] :: <<?[Int] :: <<?[Int] :: <<?[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<[Int] :: <<?[String] :: <<[Int] :: <<[Short] :: <<[Short] :: <<[Int] :: <<[Short] :: <<?[Int] :: <<[Int] :: HNil
  }
  /** Table description of table tqnpcs. Objects of this class serve as prototypes for rows in queries. */
  class Tqnpcs(tag: Tag) extends Table[TqnpcsRow](tag, "tqnpcs") {
    def * = npcid :: npctype :: ownerid :: playerid :: name :: notnpctype :: subtype :: idxserver :: mapid :: xcord :: ycord :: task0nottype :: task1 :: task2 :: task3 :: task4 :: task5 :: task6 :: task7 :: data0 :: data1 :: data2 :: data3 :: datastr :: linkid :: life :: maxlife :: direction :: flag :: itemid :: face :: HNil
    
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
    /** Database column Face Default(1) */
    val face: Column[Int] = column[Int]("Face", O.Default(1))
  }
  /** Collection-like TableQuery object for table Tqnpcs */
  lazy val Tqnpcs = new TableQuery(tag => new Tqnpcs(tag))
  
  /** Entity class storing rows of table Warehouses
   *  @param id Database column Id PrimaryKey
   *  @param password Database column Password 
   *  @param twin Database column Twin Default(0)
   *  @param phoenix Database column Phoenix Default(0)
   *  @param ape Database column Ape Default(0)
   *  @param desert Database column Desert Default(0)
   *  @param bird Database column Bird Default(0)
   *  @param stone Database column Stone Default(0)
   *  @param market Database column Market Default(0)
   *  @param mobile Database column Mobile Default(0) */
  case class WarehousesRow(id: Int, password: Option[String], twin: Int = 0, phoenix: Int = 0, ape: Int = 0, desert: Int = 0, bird: Int = 0, stone: Int = 0, market: Int = 0, mobile: Int = 0)
  /** GetResult implicit for fetching WarehousesRow objects using plain SQL queries */
  implicit def GetResultWarehousesRow(implicit e0: GR[Int], e1: GR[Option[String]]): GR[WarehousesRow] = GR{
    prs => import prs._
    WarehousesRow.tupled((<<[Int], <<?[String], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int]))
  }
  /** Table description of table warehouses. Objects of this class serve as prototypes for rows in queries. */
  class Warehouses(tag: Tag) extends Table[WarehousesRow](tag, "warehouses") {
    def * = (id, password, twin, phoenix, ape, desert, bird, stone, market, mobile) <> (WarehousesRow.tupled, WarehousesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, password, twin.?, phoenix.?, ape.?, desert.?, bird.?, stone.?, market.?, mobile.?).shaped.<>({r=>import r._; _1.map(_=> WarehousesRow.tupled((_1.get, _2, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column Id PrimaryKey */
    val id: Column[Int] = column[Int]("Id", O.PrimaryKey)
    /** Database column Password  */
    val password: Column[Option[String]] = column[Option[String]]("Password")
    /** Database column Twin Default(0) */
    val twin: Column[Int] = column[Int]("Twin", O.Default(0))
    /** Database column Phoenix Default(0) */
    val phoenix: Column[Int] = column[Int]("Phoenix", O.Default(0))
    /** Database column Ape Default(0) */
    val ape: Column[Int] = column[Int]("Ape", O.Default(0))
    /** Database column Desert Default(0) */
    val desert: Column[Int] = column[Int]("Desert", O.Default(0))
    /** Database column Bird Default(0) */
    val bird: Column[Int] = column[Int]("Bird", O.Default(0))
    /** Database column Stone Default(0) */
    val stone: Column[Int] = column[Int]("Stone", O.Default(0))
    /** Database column Market Default(0) */
    val market: Column[Int] = column[Int]("Market", O.Default(0))
    /** Database column Mobile Default(0) */
    val mobile: Column[Int] = column[Int]("Mobile", O.Default(0))
    
    /** Foreign key referencing Characters (database name f_char_id) */
    lazy val charactersFk = foreignKey("f_char_id", id, Characters)(r => r.charid, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table Warehouses */
  lazy val Warehouses = new TableQuery(tag => new Warehouses(tag))
}