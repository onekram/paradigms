get_val(		node(V, _, _, _, _, _), V).
get_key(		node(_, K, _, _, _, _), K).
get_height(	    node(_, _, H, _, _, _), H).
get_left(		node(_, _, _, L, _, _), L).
get_right(	    node(_, _, _, _, R, _), R).
get_size(		node(_, _, _, _, _, S), S).
get_height(nullptr, 0).
get_size(nullptr, 0).

map_put(nullptr, K, V, node(V, K, 1, nullptr, nullptr, 1)).
map_put(node(DV, K, DH, DL, DR, DS), K, V, node(V, K, DH, DL, DR, DS)) :- !.
map_put(node(DV, DK, DH, DL, DR, DS), K, V, CLRES) :- 
		K < DK, 
		map_put(DL, K, V, LRES),
		correct(node(DV, DK, DH, LRES, DR, DS), CLRES), !.
map_put(node(DV, DK, DH, DL, DR, DS), K, V, CRRES) :- 
		K > DK, 
		map_put(DR, K, V, RRES),
		correct(node(DV, DK, DH, DL, RRES, DS), CRRES), !.

map_get(node(V, K, _, _, _, _), K, V) :- !.
map_get(node(_, DK, _, DL, _, _), K, V) :- 
		K < DK,
		map_get(DL, K, V), !.
map_get(node(_, DK, _, _, DR, _), K, V) :- 
		K > DK,
		map_get(DR, K, V), !.

max(X, Y, X) :- X >= Y.
max(X, Y, Y) :- Y > X.

diff(nullptr, 0).
diff(node(_, _, _, L, R, _), RES) :-
		get_height(R, RH), 
		get_height(L, LH),
		RES is RH - LH.
		
correct_height(nullptr, nullptr) :- !.
correct_height(node(V, K, _, L, R, _), node(V, K, H2, L, R, S2)) :- 
		get_height(L, LH), get_height(R, RH), 
		max(LH, RH, H1), H2 is H1 + 1, 
		get_size(L, LS), get_size(R, RS), 
		S2 is LS + RS + 1, !.

right_rotate(node(V, K, H, node(LV, LK, LH, LL, LR, LS), R, S), CRES) :- 
		correct_height(node(V, K, H, LR, R, S), B),
		RES = node(LV, LK, H, LL, B, S),
		correct_height(RES, CRES).	
left_rotate(node(V, K, H, L, node(RV, RK, RH, RL, RR, RS), S), CRES) :- 
		correct_height(node(V, K, H, L, RL, S), B),
		RES = node(RV, RK, H, B, RR, S),
		correct_height(RES, CRES).

correct(node(V, K, H, L, R, S), RES) :-
    diff(node(V, K, H, L, R, S), D1), D1 = 2,
    diff(R, D2),
    (D2 < 0, right_rotate(R, NR), left_rotate(node(V, K, H, L, NR, S), RES); left_rotate(node(V, K, H, L, R, S), RES)), !.
correct(node(V, K, H, L, R, S), RES) :-
    diff(node(V, K, H, L, R, S), D1), D1 = -2,
    diff(L, D2),
    (D2 > 0, left_rotate(L, NL), right_rotate(node(V, K, H, NL, R, S), RES); right_rotate(node(V, K, H, L, R, S), RES)), !.
correct(N, NN) :- correct_height(N, NN).

deepest_left(N, N) :-
		get_left(N, nullptr), !. 
deepest_left(N, R) :-
		get_left(N, NL), deepest_left(NL, R). 

deepest_remove(node(V, K, H, nullptr, RES, S), RES, K, V) :- !. 

deepest_remove(node(V, K, H, L, R, S), CRES, RESK, RESV) :- 
		deepest_remove(L, RES, RESK, RESV), 
		correct(node(V, K, H, RES, R, S), CRES), !. 

map_remove(nullptr, _, nullptr) :- !.
map_remove(node(_, KQ, _, nullptr, nullptr, _), KQ, nullptr) :- !.
map_remove(node(_, KQ, _, L, nullptr, _), KQ, L) :- !.
map_remove(node(_, KQ, _, nullptr, R, _), KQ, R) :- !.

map_remove(node(V, KQ, H, L, R, S), KQ, CRES) :-
		deepest_remove(R, RES, DPK, DPV), 
		correct(node(DPV ,DPK, H, L, RES, S), CRES), !.
map_remove(node(V, K, H, L, R, S), KQ, CLRES) :- 
		KQ < K,
		map_remove(L, KQ, LRES), 
		correct(node(V, K, H, LRES, R, S), CLRES), !.
map_remove(node(V, K, H, L, R, S), KQ, CRRES) :- 
		KQ > K,
		map_remove(R, KQ, RRES),
		correct(node(V, K, H, L, RRES, S), CRRES), !.

min_entry((K1, V1), (K2, _), (K1, V1)) :- 
		K1 =< K2, !.
min_entry((K1, _), (K2, V2), (K2, V2)) :- 
		K2 < K1, !.
			
map_ceilingEntry(node(V, K, _, _, _, _), K, (K, V)) :- !.
map_ceilingEntry(node(V, K, _, L, R, _), KQ, RES) :- 
		KQ > K,
		map_ceilingEntry(R, KQ, RES), !.
map_ceilingEntry(node(V, K, _, nullptr, R, _), KQ, (K, V)) :- KQ < K, !.
map_ceilingEntry(node(V, K, _, L, R, _), KQ, RES) :- 
		KQ < K,
		(map_ceilingEntry(L, KQ, (PK, PV)), min_entry((PK, PV), (K, V), RES), !; 
		RES = (K, V)).
		
map_removeCeiling(N, KQ, RES) :-
		map_ceilingEntry(N, KQ, (RK, RV)), 
		map_remove(N, RK, RES), !.
map_removeCeiling(N, _, N) :- !.

map_headMapSize(nullptr, _, 0) :- !.
map_headMapSize(node(_, TK, _, L, _, _), TK, RS) :-
		get_size(L, RS), !.
map_headMapSize(node(_, K, _, L, _, _), TK, RS) :-
		K > TK,
		map_headMapSize(L, TK, RS), !.
map_headMapSize(node(_, K, _, L, R, _), TK, RS) :-
		K < TK,
		get_size(L, LS), 
		map_headMapSize(R, TK, RRS), 
		RS is RRS + LS + 1, !.

map_tailMapSize(N, FK, RES) :-
		get_size(N, SZ), 
		map_headMapSize(N, FK, HRES), 
		RES is SZ - HRES.

map_build([], nullptr).
map_build([(K, V) | T], R1) :- map_build(T, R), map_put(R, K, V, R1).
																			