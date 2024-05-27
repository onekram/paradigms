:- load_library('alice.tuprolog.lib.DCGLibrary').

to_lower(C, LC) :-
    char_code(C, CC),
    (CC >= 65, CC =< 90 ->  
        LCC is CC + 32,
        char_code(LC, LCC);
    LC = C).

lookup(K, [(K1, V) | _], V) :- atom_chars(K, [H | _]), to_lower(H, K1).
lookup(K, [_ | T], V) :- lookup(K, T, V).

ws --> [].
ws --> [' '], ws.

expr_p(variable(N)) --> 
		{ nonvar(N) -> call(atom_chars(N, CS)); var(N) },
		ws, var_p(CS), ws,
		{ CS = [_ | _], atom_chars(N, CS) }.

var_p([]) --> [].
var_p([H | T]) -->
  { member(H, [x, y, z, 'X', 'Y', 'Z']) },
  [H], var_p(T).

		
number_chars_minus('-', ['-']) :- !.
number_chars_minus(V, CS) :- number_chars(V, CS).

expr_p(const(V)) -->
  { nonvar(V) -> call(number_chars(V, CS)); var(V) },
  ws, digits_p(CS), ws, 
  { CS = [_ | _], number_chars_minus(V, CS) }.
  
digits_p([]) --> [].
digits_p([H | T]) -->
  { member(H, ['-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.']) },
  [H], digits_p(T).

op_p(op_bitif) --> ['?'].
op_p(op_bitmux) --> ['¿'].

op_p(op_add) --> ['+'].
op_p(op_subtract) --> ['-'].
op_p(op_multiply) --> ['*'].
op_p(op_divide) --> ['/'].
op_p(op_bitand) --> ['&'].
op_p(op_bitor) --> ['|'].
op_p(op_bitxor) --> ['^'].

op_p(op_negate) --> [n, e, g, a, t, e].
op_p(op_bitnot) --> ['~'].

expr_p(operation(OP, A, B, C)) --> 
		ws, ['('], 
		ws, expr_p(A), [' '], 
		ws, op_p(OP), ws, 
		[' '], expr_p(B), 
		ws, [' '], [':'], [' '], ws,
		expr_p(C), ws,
		[')'], ws.
		
expr_p(operation(OP, A, B)) --> 
		ws, ['('], 
		ws, expr_p(A), [' '], 
		ws, op_p(OP), ws, 
		[' '], expr_p(B), 
		ws, [')'], ws.
		
expr_p(operation(OP, A)) --> 
		ws, op_p(OP), 
		ws, [' '], 
		expr_p(A), ws.

infix_str(E, A) :- ground(E),
		phrase(expr_p(E), C), atom_chars(A, C), !.
		
infix_str(E, A) :- atom(A),
		atom_chars(A, C), phrase(expr_p(E), C), !.

evaluate(const(Value), _, Value).
evaluate(variable(Name), VS, V) :- 
		lookup(Name, VS, V).

evaluate(operation(OP, A, B, C), VS, R) :-
		evaluate(A, VS, RA), 
		evaluate(B, VS, RB), 
		evaluate(C, VS, RC), 
		op_impl(OP, RA, RB, RC, R).
		
evaluate(operation(OP, A, B), VS, R) :-
		evaluate(A, VS, RA), 
		evaluate(B, VS, RB), 
		op_impl(OP, RA, RB, R).

evaluate(operation(OP, A), VS, R) :- 
		evaluate(A, VS, RA), 
		op_impl(OP, RA, R).

op_impl(op_bitif, A, B, C, R) :- A /\ 1 =:= 1 -> R = B; R = C.
op_impl(op_bitmux, A, B, C, R) :- R is (\A /\ B) \/ (A /\ C).
op_impl(op_add, A, B, R) :- R is A + B.
op_impl(op_subtract, A, B, R) :- R is A - B.
op_impl(op_multiply, A, B, R) :- R is A * B.
op_impl(op_divide, A, B, R) :- B =:= 0 -> R = 0; R is A / B.
op_impl(op_bitand, A, B, R) :- R is A /\ B.
op_impl(op_bitor, A, B, R) :- R is A \/ B.
op_impl(op_bitxor, A, B, R) :- R is (A \/ B) /\ \(A /\ B).

op_impl(op_negate, A, R) :- R is -A.
op_impl(op_bitnot, A, R) :- R is \A.
