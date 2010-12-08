package pfc                                 

import org.scalatest.Spec
import org.scalatest.matchers.ShouldMatchers     

private case class Term(coef: Double, exp: Int) {
	require(coef != 0 && exp >= 0)   
	                 
	private def printCoefWithCorrectFormat = {   
		val coefInt: Int = coef.toInt
		val coefDouble: Double = coefInt
	
		if(coef == coefDouble) 
			coefInt.abs.toString
		else
			coef.abs.toString
	 }
	
	private def printCoef = 
		if(coef.abs != 1 || exp == 0) 
			printCoefWithCorrectFormat 
		else ""
	
	private def printX = if(exp != 0) "x" else ""
	                          
	private def printExp = if(exp != 1 && exp != 0) "^" + exp.toString else ""
	
	override def toString = printCoef + printX + printExp 
	
	def + (that: Term) = {                  
		require(this.exp == that.exp)
		Term(this.coef + that.coef, this.exp)
	}
}

class Pol private (private val terms: List[Term]) {

  // construtor auxiliar
  // (n.b.: tanto o construtor primario como o auxiliar sao privados)
	private def this(coef: Double, exp: Int) = this(List(Term(coef, exp)))
	
//
//  // aritmetica de polinomios  
  	def + (that: Pol): Pol = (this.terms, that.terms) match {
		case (Nil, Nil) => new Pol(Nil)
		case (Nil, h::t) => that
		case (h::t, Nil) => this        
		case (Term(c1,e1)::t1, Term(c2,e2)::t2) => {
			if (e1 == e2) {      
				val sum = c1 + c2
 				val rest = new Pol(t1) + new Pol(t2)
				if(sum == 0)
					new Pol(rest.terms)
				else
					new Pol(Term(c1 + c2, e1)::rest.terms)   			
			}
			else if(e1 > e2){                 
				val rest = new Pol(t1) + that
				new Pol(Term(c1,e1)::rest.terms)
			}
			else {
				val rest = this + new Pol(t2)
				new Pol(Term(c2,e2)::rest.terms)
			}
		}
	}        
	
	
	def - (that: Pol): Pol = this + (-that)   
	
//  def * (that: Pol): Pol
//  def / (that: Pol): Tuple2[Pol, Pol]
//
//  // operadores unarios	
  	def unary_+ : Pol = this
 	def unary_- : Pol = this * -1

//
//  // aritmetica mista (o operando 1 e' um polinomio, o operando 2 e' um numero)
//  def + (d: Double): Pol
//  def - (d: Double): Pol
  	def * (d: Double): Pol = {    
		new Pol(this.terms.map(term => Term(term.coef * d, term.exp)))
	}
//  def / (d: Double): Pol
//
//  // grau, potenciacao e derivacao
//  def degree: Int
//  def ^(n: Int): Pol
//  def deriv: Pol
//  def ! : Pol
//
//  // calcula o valor do polinomio alvo para um dado valor de x
//  def apply(x: Double): Double
//
//  // composicao do polinomio alvo com outro polinomio
//  def apply(that: Pol): Pol
//
//  // sobrescrita de metodos da classe Any
  	override def equals(other: Any): Boolean = other match{
		case that:Pol => {this.terms == that.terms} 
		case _ => false
	}
//  override def hashCode: Int

	def toStringR :String = {
		if(terms.isEmpty)
			""
		else {	                  
			if(terms.head.coef < 0)
				" - " + terms.head.toString + new Pol(terms.tail).toStringR
			else
				" + " + terms.head.toString + new Pol(terms.tail).toStringR
		}		
	}   
	
 	override def toString = {
		if(terms.isEmpty)
			"0"
		else if(terms.head.coef < 0)
			"-" + terms.head.toString + new Pol(terms.tail).toStringR
		else
			terms.head.toString + new Pol(terms.tail).toStringR
	}
//
//  // metodo auxiliar que multiplica o polinomio alvo por um termo simples
//  private def * (term: Term): Pol
}

object Pol {
	def apply(coef: Double, exp:Int) = new Pol(coef, exp)
	
	
  // conversao implicita de Double em Pol
//  implicit def doubleToPol(d: Double): Pol
//
//  // metodos de fabrica acessiveis para os clientes
//  def apply(coef: Double, exp: Int): Pol
//  def apply(coef: Double): Pol
//
//  // metodo de fabrica interno (serve apenas para evitar o uso de new)
//  private def apply(terms: List[Term]): Pol
//
//  // metodo auxiliar para as operacoes de adicao e subtracao de polinomios
//  private def add(terms1: List[Term], terms2: List[Term]): List[Term]
}
   


class PolSpec extends Spec with ShouldMatchers {
	
	describe ("A Term") {
 		it ("should have a coef and an exp") {
			val term = Term(2,3)
			term.coef should equal (2)
			term.exp should equal (3)
		}    
	
		it ("should not have a coef equal to zero") {
			evaluating {Term(0,3)} should produce [IllegalArgumentException]
		}                                        
	
		it ("should not have a exp lesser than zero") {
			evaluating {Term(2,-3)} should produce [IllegalArgumentException]
		}
	                                  
		it ("should print itself hiding the coef and exp when (1,1)") {
			val term = Term(1,1)
			term.toString should equal ("x")
		}                      
	
		it ("should print itself hiding the coef when (1,2)") {
			val term = Term(1,2)
			term.toString should equal ("x^2")
		}        
	
		it ("should print itself hiding the exp when (2,1)") {
			val term = Term(2,1)
			term.toString should equal ("2x")
		}
	     
		it ("should print itself when (2.357,2)") {
			val term = Term(2.357,2)
			term.toString should equal ("2.357x^2")
		}     
	
		it ("should print itself when (1,0)") {
			val term = Term(1,0)
			term.toString should equal ("1") 	  
		}                                    
	
		it ("should print its own module when (-2,3)") {
			val term = Term(-2,3)
			term.toString should equal ("2x^3") 	  
		}                               
	
		it ("should know how to sum to another") {
			val termA = Term(2,3)
			val termB = Term(2,3)		
			(termA + termB) should equal (Term(4,3)) 	  
		}
	}	                                  
	
	describe ("A Pol") {
		it ("should be instantiated with its companion obejct") {
			Pol(2,3)
		}         
	
		describe ("(when printing)"){
			it ("should print its only term") {
				val pol = Pol(2,3)
				pol.toString should equal ("2x^3")
			}                                     
			
		    it ("should know how to print Nil Pol") {
				val polA = Pol(2,2)
				val polB = Pol(-2,2)
				(polA + polB).toString should equal ("0")		
			}
		}
	
		describe ("(when comparing)"){
			it ("should know how to compare to another") {
				val polA = Pol(2,3)
				val polB = Pol(2,3)
				(polA == polB) should equal (true)
			} 
	    }
	
		describe ("(when summing)"){
			it ("should know how to sum two terms") {
				val polA = Pol(2,3)
				val polB = Pol(2,3)
				(polA + polB) should equal (Pol(4,3))
			}
	                            
			it ("should know how to sum two different terms") {
				val polA = Pol(1,3)
				val polB = Pol(2,3)
				(polA + polB) should equal (Pol(3,3))
			}      
	
			it ("should know how to sum two terms with diffent exp") {
				val polA = Pol(1,1)
				val polB = Pol(2,2)
				(polA + polB).toString should equal ("2x^2 + x")
			}             
	
			it ("should know how to sum two Pols iguais with 3 terms each") {
				val polA = Pol(2,2) + Pol(1,1) + Pol(1,0)
				val polB = Pol(2,2) + Pol(1,1) + Pol(1,0)
				(polA + polB).toString should equal ("4x^2 + 2x + 2")
			}                   
	
			it ("should know how to sum two Pols with different number of terms") {
				val polA = Pol(2,2) + Pol(1,1) + Pol(1,0)
				val polB = Pol(2,3) + Pol(1,1)
				(polA + polB).toString should equal ("2x^3 + 2x^2 + 2x + 1")		
			}

			it ("should know how to sum two Pols with negative coefs") {
				val polA = Pol(-2,2) + Pol(1,1) + Pol(-4,0)
				val polB = Pol(3,2) + Pol(-3,1) + Pol(3,0)
				(polA + polB).toString should equal ("x^2 - 2x - 1")		
			}  
			
			it ("should know how to filter null terms") {
				val polA = Pol(2,2) + Pol(1,1)
				val polB = Pol(-2,2)
				(polA + polB).toString should equal ("x")		
			}    
	    } 
	
		describe ("(when creating itself)"){	             
			it ("should know how to create a Pol reverse order of exp") {
				val polA = Pol(-2,3) + Pol(1,1) + Pol(-4,4)
				(polA).toString should equal ("-4x^4 - 2x^3 + x")		
			}
		} 
		
		describe ("(when multiplying)"){
			it ("should know how to multiply an only term Pol by a number") {
				val polA = Pol(1,1)
				(polA * 2).toString should equal ("2x")
			} 
			      
			it ("should know how to multiply a two terms Pol by a number") {
				val polA = Pol(2,2) + Pol(1,1)
				(polA * 2).toString should equal ("4x^2 + 2x")
			}
		}       
		
		describe ("(when using a unary operator)"){
			it ("should know how to negate an only term Pol by a number") {
				val polA = Pol(1,1)
				(-polA).toString should equal ("-x")
			}        
			
			it ("should know how to use plus with an only term Pol by a number") {
				val polA = Pol(1,1)
				(+polA).toString should equal ("x")
			}        
		}
		
		describe ("(when subtracting)"){
			it ("should know how to subtract two Pols") {
				val polA = Pol(2,5) + Pol(1,4) + Pol(1,3)                
				val polB = Pol(1,5) + Pol(2,4) + Pol(1,2) + Pol(-1,1)            				
				(polA - polB).toString should equal ("x^5 - x^4 + x^3 - x^2 + x")
			}               
		}
	}
}




